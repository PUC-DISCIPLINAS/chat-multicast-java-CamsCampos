package componentes;

import java.util.ArrayList;

public class ChatRoom {

	private String name;
	private String id;
	private ArrayList<String> members;

	public ChatRoom(String name, String id) {
		this.name = name;
		this.id = id;
		members = new ArrayList<String>();
	}

	public void addMember(String name) {
		members.add(name);
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public ArrayList<String> getMembers() {
		return this.members;
	}

}
