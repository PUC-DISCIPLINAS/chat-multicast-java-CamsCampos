package redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import componentes.ChatRoom;

public class Server {

	static ArrayList<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
	static int[] startIpMulticast = { 224, 0, 0, 0 };
	static int maxPrimeiroOctetoIpMulticast = 239;
	static int serverPort = 7575;

	public static void main(String args[]) {
		DatagramSocket socket = null;
		DatagramPacket req = null;

		String message;
		byte[] buffer;

		try {
			socket = new DatagramSocket(serverPort);
			System.out.println("<<Server>> ouvindo na porta " + serverPort);

			while (true) {

				buffer = new byte[10000];

				req = new DatagramPacket(buffer, buffer.length);
				socket.receive(req);
				message = new String(req.getData()).trim();

				socket.send(broker(message, req));

			}

		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	public static String getIpMulticast() {
		for (int i = 3; i > 1; i--) {
			if (startIpMulticast[i] < maxPrimeiroOctetoIpMulticast) {
				startIpMulticast[i]++;
				return arrayMulticastToString(startIpMulticast);
			}
		}
		if (startIpMulticast[0] < maxPrimeiroOctetoIpMulticast) {
			startIpMulticast[0]++;
			return arrayMulticastToString(startIpMulticast);
		}
		return "";

	}

	public static String arrayMulticastToString(int[] arrayM) {

		String multicastString = Integer.toString(arrayM[0]);

		for (int i = 1; i < 4; i++) {
			multicastString += "." + Integer.toString(arrayM[i]);
		}

		return multicastString;

	}

	public static DatagramPacket broker(String message, DatagramPacket req) {

		String comando = getComando(message);
		String params = getParams(message);

		byte[] error;

		switch (comando) {

		case "criarSala":
			return criarSala(params, req);
		case "listarSalas":
			return listarSalas(req);
		case "listarMembrosSala":
			return listarMembrosSala(params, req);
		case "adicionarUsuario":
			return adicionarUsuario(params, req);
		case "enviarMensagem":

		default:
			error = new String("Erro: comando (" + comando + ") inexistente").getBytes();
			return new DatagramPacket(error, req.getLength(), req.getAddress(), req.getPort());
		}
	}

	public static String getComando(String input) {
		int indexOf = input.indexOf(" ");

		if (indexOf != -1)
			return input.substring(0, indexOf);
		else
			return input;
	}

	public static String getParams(String input) {
		int indexOf = input.indexOf(" ");

		if (indexOf != -1)
			return input.substring(indexOf);
		else
			return input;
	}

	public static DatagramPacket criarSala(String params, DatagramPacket req) {
		String nomeSala = params.split("=")[1];

		String ip = getIpMulticast();

		chatRooms.add(new ChatRoom(nomeSala, ip));

		byte[] res = new String("<<de Servidor>> Sala \"" + nomeSala + "\" foi criada com sucesso!").getBytes();

		return new DatagramPacket(res, res.length, req.getAddress(), req.getPort());

	}

	public static DatagramPacket listarSalas(DatagramPacket req) {
		String rooms = "Salas:";
		byte[] res = null;

		for (int i = 0; i < chatRooms.size(); i++) {
			ChatRoom room = chatRooms.get(i);
			rooms += new String("\n  " + (i + 1) + ". ID = " + room.getId() + " | Nome: " + room.getName());
		}

		res = rooms.getBytes();

		return new DatagramPacket(res, res.length, req.getAddress(), req.getPort());

	}

	public static DatagramPacket adicionarUsuario(String params, DatagramPacket req) {

		String paramStr = params.substring(1);

		int indexOf = paramStr.indexOf(" ");
		String roomId = paramStr.substring(0, indexOf);
		String userName = paramStr.substring(indexOf);

		boolean salaExiste = false;
		byte[] res;

		for (ChatRoom cr : chatRooms) {
			if (cr.getId().equals(roomId)) {
				salaExiste = true;
				cr.addMember(userName);
			}
		}
		if (salaExiste == true)
			res = new String("1").getBytes();
		else
			res = new String("0").getBytes();

		return new DatagramPacket(res, res.length, req.getAddress(), req.getPort());

	}

	public static DatagramPacket listarMembrosSala(String params, DatagramPacket req) {

		ArrayList<String> members = null;
		byte[] res;

		String roomId = params.split("=")[1];

		for (ChatRoom c : chatRooms) {
			if (c.getId().equals(roomId))
				members = c.getMembers();
		}

		if (members == null)
			res = new String("Sala " + roomId + " não existe").getBytes();
		else {
			String stringMembers = "Membros da sala: ";

			for (int i = 0; i < members.size(); i++) {
				String member = members.get(i);
				stringMembers += new String("\n  " + (i + 1) + ". " + member);
			}

			res = stringMembers.getBytes();

		}

		return new DatagramPacket(res, res.length, req.getAddress(), req.getPort());

	}

}
