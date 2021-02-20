package redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast {

	static int multicastPort = 8888;
	MulticastSocket socket = null;

	byte[] buffer;
	DatagramPacket messageIn;
	DatagramPacket messageOut;

	public Multicast() {

		try {
			socket = new MulticastSocket(multicastPort);
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		}

	}

	public void join(String userName, String roomId) {
		try {
			InetAddress inetAddresGroup = InetAddress.getByName(roomId);
			socket.joinGroup(inetAddresGroup);

			byte[] byteMessage = new String("Usuário [" + userName + "] entrou na sala " + roomId).getBytes();

			messageOut = new DatagramPacket(byteMessage, byteMessage.length, inetAddresGroup, multicastPort);
			socket.send(messageOut);

			while (true) {
				buffer = new byte[100000];
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
				socket.receive(messageIn);
				System.out.println("<<Multicast>> " + new String(messageIn.getData()).trim());
			}

		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		}

	}

	public void sendMessage(String roomId, String userName, String message) {

		try {
			InetAddress inetAddresGroup = InetAddress.getByName(roomId);

			byte[] messageByte = new String("| [" + userName + "]: " + message).getBytes();
			messageOut = new DatagramPacket(messageByte, messageByte.length, inetAddresGroup, multicastPort);
			socket.send(messageOut);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void leave(String roomId, String userName) {
		try {
			InetAddress inetAddresGroup = InetAddress.getByName(roomId);

			if (socket != null) {
				if (inetAddresGroup != null) {
					byte[] byteMessage = new String("Usuário [" + userName + "] saindo da sala " + roomId).getBytes();
					messageOut = new DatagramPacket(byteMessage, byteMessage.length, inetAddresGroup, multicastPort);
					socket.send(messageOut);
					socket.leaveGroup(inetAddresGroup);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeSocketMulticast() {
		if (socket != null)
			socket.close();
	}

}
