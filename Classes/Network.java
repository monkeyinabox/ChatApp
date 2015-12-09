package ChatApp.Classes;

import java.io.*;
import java.util.*;
import java.net.*;

public interface Network {

	static final ArrayList peerList;

	void sendMessage();

	void reciveMessage();

	void discoverPeers();

	void peerAdd();

	void peerDead();

}
