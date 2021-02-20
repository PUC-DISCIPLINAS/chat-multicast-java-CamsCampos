package redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	static int serverPort = 7575;
	static Multicast multicast;
	static ListenMulticast listenMulticast;
	static InetAddress addressHost;
	static DatagramSocket socket = null;
	static String userName = null;
	static Scanner sc = new Scanner(System.in);

	public static void main(String args[]) {

		try {

			multicast = new Multicast();
			addressHost = InetAddress.getByName(args[0]);
			userName = args[1];
			socket = new DatagramSocket();

			System.out.println("\n ==== Sistema Inicializado | Bem-vindo(a) " + userName + " ====\n");
			broker();

		} catch (IOException e) {
			System.out.println("IO error Client: " + e.getMessage());
		} finally {
			if (socket != null)
				socket.close();
			multicast.closeSocketMulticast();
		}

	}

	public static void broker() {
		String entry = userEntry();

		String comando = getComando(entry);
		String params = getParams(entry);

		while (comando != "sair") {

			switch (comando) {

			case "criarSala":
				criarSala(params);
				break;
			case "listarSalas":
				listarSalas();
				break;
			case "listarMembrosSala":
				listarMembrosSala(params);
				break;
			case "entrarNaSala":
				entrarNaSala(params);
				break;
			case "sairDaSala":
				sairDaSala(params);
				break;
			case "enviarMensagem":
				enviarMensagem(params);
				break;

			default:
				System.out.println("<<Client>> Erro: comando \"" + comando + "\" inexistente");
			}

			entry = userEntry();
			comando = getComando(entry);
			params = getParams(entry);
		}

		System.out.println("\n ==== Fim do Sistema ====\n");
	}

	public static String userEntry() {

		String entry = sc.nextLine();
		return entry;

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

		if (indexOf != -1) {
			return input.substring(indexOf);
		} else
			return input;
	}

	public static void criarSala(String params) {

		String nomeSala = params.split("=")[1].trim();
		byte[] buffer;

		try {
			byte[] reqMessage = new String("criarSala nome=" + nomeSala).getBytes();
			DatagramPacket req = new DatagramPacket(reqMessage, reqMessage.length, addressHost, serverPort);
			socket.send(req);

			buffer = new byte[1000];
			DatagramPacket res = new DatagramPacket(buffer, buffer.length);
			socket.receive(res);
			System.out.println(new String(res.getData()).trim());
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}

	}

	public static void listarSalas() {

		byte[] buffer;

		try {
			byte[] reqMessage = new String("listarSalas").getBytes();
			DatagramPacket req = new DatagramPacket(reqMessage, reqMessage.length, addressHost, serverPort);
			socket.send(req);

			buffer = new byte[1000];
			DatagramPacket res = new DatagramPacket(buffer, buffer.length);
			socket.receive(res);
			System.out.println(new String(res.getData()).trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void listarMembrosSala(String params) {

		String roomId = params.split("=")[1].trim();
		byte[] buffer;

		try {
			byte[] reqMessage = new String("listarMembrosSala id=" + roomId).getBytes();
			DatagramPacket req = new DatagramPacket(reqMessage, reqMessage.length, addressHost, serverPort);
			socket.send(req);

			buffer = new byte[1000];
			DatagramPacket res = new DatagramPacket(buffer, buffer.length);
			socket.receive(res);
			System.out.println(new String(res.getData()).trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void entrarNaSala(String params) {

		String roomId = params.split("=")[1].trim();

		byte[] buffer;
		String status = null;

		try {
			byte[] reqMessage = new String("adicionarUsuario " + roomId + " " + userName).getBytes();
			DatagramPacket req = new DatagramPacket(reqMessage, reqMessage.length, addressHost, serverPort);
			socket.send(req);

			buffer = new byte[1000];
			DatagramPacket res = new DatagramPacket(buffer, buffer.length);
			socket.receive(res);
			status = new String(res.getData()).trim();

			if (status.equals("1")) {
				listenMulticast = new ListenMulticast(multicast, userName, roomId);
			} else
				System.out.println("<<Client>> sala " + roomId + " não existe");

		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());

		}
	}

	public static void enviarMensagem(String params) {

		String paramStr = params.substring(1);
		int indexOf = paramStr.indexOf(" ");

		String param1 = paramStr.substring(0, indexOf);
		String param2 = paramStr.substring(indexOf);

		String roomId = param1.split("=")[1].trim();
		String message = param2.split("=")[1];

		multicast.sendMessage(roomId, userName, message);

	}

	public static void sairDaSala(String params) {

		String roomId = params.split("=")[1].trim();
		multicast.leave(roomId, userName);
	}

}

class ListenMulticast extends Thread {

	String userName;
	String roomId;
	Multicast multicast;

	public ListenMulticast(Multicast multicast, String userName, String roomId) {
		this.userName = userName;
		this.roomId = roomId;
		this.multicast = multicast;
		this.start();
	}

	@Override
	public void run() {
		multicast.join(userName, roomId);
	}
}