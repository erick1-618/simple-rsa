package br.com.erick.vision;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.com.erick.rsa.Key;
import br.com.erick.rsa.Key.usage;
import br.com.erick.rsa.MessageParser;
import br.com.erick.rsa.RSACrypto;
import br.com.erick.vision.Button.actions;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private Key publicKey;
	private Key privateKey;

	private JTextArea plainText;
	private JTextArea cypherText;
	
	private	JFrame keysInfoFrame;


	public MainWindow() {

		setLayout(new BorderLayout());

		JPanel buttonsGrid = new JPanel();
		buttonsGrid.setLayout(new GridLayout());
		buttonsGrid.add(new Button(actions.GENERATE_KEYS, this));
		buttonsGrid.add(new Button(actions.EXPORTS_KEYS, this));
		buttonsGrid.add(new Button(actions.IMPORT_PUBLIC_KEY, this));
		buttonsGrid.add(new Button(actions.IMPORT_PRIVATE_KEY, this));
		buttonsGrid.add(new Button(actions.SHOW_KEYS_INFO, this));

		JPanel cryptBox = new JPanel();
		cryptBox.setLayout(new BorderLayout());
		cryptBox.add(new Button(actions.ENCRYPT, this), BorderLayout.NORTH);
		this.plainText = new JTextArea();
		plainText.setLineWrap(true);
		plainText.setWrapStyleWord(true);
		JScrollPane scrollPaneA = new JScrollPane(plainText);
		scrollPaneA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cryptBox.add(scrollPaneA, BorderLayout.CENTER);

		JPanel decryptBox = new JPanel();
		decryptBox.setLayout(new BorderLayout());
		decryptBox.add(new Button(actions.DECRYPT, this), BorderLayout.NORTH);
		this.cypherText = new JTextArea();
		cypherText.setLineWrap(true);
		cypherText.setWrapStyleWord(true);
		JScrollPane scrollPaneB = new JScrollPane(cypherText);
		scrollPaneB.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneB.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		decryptBox.add(scrollPaneB, BorderLayout.CENTER);

		JPanel centerBox = new JPanel();
		centerBox.setLayout(new GridLayout(2, 1));
		centerBox.add(cryptBox);
		centerBox.add(decryptBox);

		add(buttonsGrid, BorderLayout.NORTH);
		add(centerBox, BorderLayout.CENTER);

		setTitle("RSA Simple Crypto");
		setSize(new Dimension(900, 600));
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void performAction(Button.actions action) {
		switch (action) {
		case ENCRYPT:
			if (this.publicKey == null) {
				JOptionPane.showMessageDialog(this, "Public key not selected", "Public key's missing",
						JOptionPane.ERROR_MESSAGE);
				break;
			}
			if(this.publicKey.isValid()) {
				JOptionPane.showMessageDialog(this, "Public key not valid", "Encryption error",
						JOptionPane.ERROR_MESSAGE);
				break;				
			}
			BigInteger cyphered = RSACrypto.crypt(MessageParser.StringToBigInteger(this.plainText.getText()),
					this.publicKey);
			this.cypherText.setText(cyphered.toString(36));
			cypherText.revalidate();
			break;
		case DECRYPT:
			if (this.privateKey == null) {
				JOptionPane.showMessageDialog(this, "Private key not selected", "Private key's missing",
						JOptionPane.ERROR_MESSAGE);
				break;
			}
			if(this.privateKey.isValid()) {
				JOptionPane.showMessageDialog(this, "Private key not valid", "Decryption error",
						JOptionPane.ERROR_MESSAGE);
				break;				
			}
			BigInteger decyphered = RSACrypto.decrypt(new BigInteger(this.cypherText.getText(), 36), privateKey);
			this.plainText.setText(MessageParser.BigIntegerToString(decyphered));
			plainText.revalidate();
			break;
		case EXPORTS_KEYS:
			if (this.privateKey == null || this.publicKey == null) {
				JOptionPane.showMessageDialog(this, "No keys to export", "No Keys", JOptionPane.ERROR_MESSAGE);
				break;
			}
			JFileChooser fileChooser = new JFileChooser();
			JOptionPane.showMessageDialog(this, "Select a directory to save your keys", "Export keys",
					JOptionPane.INFORMATION_MESSAGE);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFolder = fileChooser.getSelectedFile();
				try (FileOutputStream fos = new FileOutputStream(
						selectedFolder.getAbsolutePath() + "/" + this.publicKey.getName() + ".key");
						FileOutputStream fos2 = new FileOutputStream(
								selectedFolder.getAbsolutePath() + "/" + this.privateKey.getName() + ".key")) {
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
					oos.writeObject(publicKey);
					oos2.writeObject(privateKey);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Error on exporting key", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		case GENERATE_KEYS:
			if(publicKey != null || privateKey != null) {
				int choice = JOptionPane.showConfirmDialog(this, "Current keys will be lost. Proceed?", "Warning", JOptionPane.OK_CANCEL_OPTION);				
				if(choice != JOptionPane.OK_OPTION) break;
			}
			String name = JOptionPane.showInputDialog(this, "Enter the key's owner");
			if (name == null)
				break;
			if (name.isBlank()) {
				JOptionPane.showMessageDialog(this, "The key's owner wiil be anonymous", "Owner Name",
						JOptionPane.WARNING_MESSAGE);
				name = "anonymous";
			}
			if (!name.matches("^[a-zA-Z\s]+$")) {
				JOptionPane.showMessageDialog(this, "Owner's name must not be empty\nAnd contain only letters",
						"Owner name error", JOptionPane.ERROR_MESSAGE);
				break;
			}
			int returnChoice = JOptionPane.showConfirmDialog(this, "Set a expiration time for the generated keys?",
					"Generated keys", JOptionPane.YES_NO_OPTION);
			LocalDateTime expirationDate = null;
			if (returnChoice == JOptionPane.YES_OPTION) {
				while (true) {
					String date = JOptionPane.showInputDialog(this, "Enter the expiration time: (YYYYMMDD)",
							"Expiration Time", JOptionPane.QUESTION_MESSAGE);
					try {
						if (date == null)
							break;
						expirationDate = LocalDateTime.of(Integer.parseInt(date.substring(0, 4)),
								Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8)), 0, 0);
						if (expirationDate.isBefore(LocalDateTime.now())) {
							throw new DateTimeException("The date is before today");
						}
						break;
					} catch (DateTimeException | NumberFormatException | StringIndexOutOfBoundsException e) {
						JOptionPane.showMessageDialog(this, "Enter a valid expiration date", "Date error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			name = name.trim().replace(' ', '_').toUpperCase();
			name += "_#_";
			name += UUID.randomUUID().toString();
			Key[] keys = RSACrypto.getKeyPair(name, expirationDate);
			this.publicKey = keys[0];
			this.privateKey = keys[1];
			break;
		case IMPORT_PRIVATE_KEY:
			JFileChooser importChooser = new JFileChooser();
			importChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue2 = importChooser.showOpenDialog(this);
			if (returnValue2 == JFileChooser.APPROVE_OPTION) {
				File file = importChooser.getSelectedFile();
				try (FileInputStream fis = new FileInputStream(file)) {
					ObjectInputStream ois = new ObjectInputStream(fis);
					Key key = (Key) ois.readObject();
					if (key.getUsage() == usage.PUBLIC) {
						int option = JOptionPane.showConfirmDialog(this,
								"That's a public key \nThis import must cause issues\nKeep on import?",
								"Importing key alert", JOptionPane.OK_CANCEL_OPTION);
						if (option == JOptionPane.CANCEL_OPTION)
							break;
					}
					this.privateKey = key;
				} catch (IOException | ClassNotFoundException e) {
					JOptionPane.showMessageDialog(this, "Error on importing key", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		case IMPORT_PUBLIC_KEY:
			JFileChooser importChooser2 = new JFileChooser();
			importChooser2.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue3 = importChooser2.showOpenDialog(this);
			if (returnValue3 == JFileChooser.APPROVE_OPTION) {
				File file = importChooser2.getSelectedFile();
				try (FileInputStream fis = new FileInputStream(file)) {
					ObjectInputStream ois = new ObjectInputStream(fis);
					Key key = (Key) ois.readObject();
					if (key.getUsage() == usage.PRIVATE) {
						int option = JOptionPane.showConfirmDialog(this,
								"That's a private key \nThis import must cause issues\nKeep on import?",
								"Importing key alert", JOptionPane.OK_CANCEL_OPTION);
						if (option == JOptionPane.CANCEL_OPTION)
							break;
					}
					this.publicKey = key;
				} catch (IOException | ClassNotFoundException e) {
					JOptionPane.showMessageDialog(this, "Error on importing key", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			break;
		case SHOW_KEYS_INFO:
			if(publicKey == null || privateKey == null) {
				JOptionPane.showMessageDialog(this, "Public or private key not selected", "Error", JOptionPane.ERROR_MESSAGE);
				break;
			}
			if(keysInfoFrame != null) {
				if(keysInfoFrame.isShowing()) {
					keysInfoFrame.dispose();
				}				
			}
			keysInfoFrame = new JFrame();
			keysInfoFrame.setLocationRelativeTo(this);
			keysInfoFrame.setResizable(false);
			keysInfoFrame.setLayout(new GridLayout(3, 3));
			keysInfoFrame.add(new JLabel("OWNER"));
			keysInfoFrame.add(new JLabel("EXPIRATION"));
			keysInfoFrame.add(new JLabel("USAGE"));
			keysInfoFrame.add(new JLabel(publicKey.getOwner()));
			keysInfoFrame.add(new JLabel(publicKey.getExpirationDate()));
			keysInfoFrame.add(new JLabel("PUBLIC"));
			keysInfoFrame.add(new JLabel(privateKey.getOwner()));
			keysInfoFrame.add(new JLabel(privateKey.getExpirationDate()));
			keysInfoFrame.add(new JLabel("PRIVATE"));
			keysInfoFrame.setSize(new Dimension(500, 120));
			keysInfoFrame.setTitle("Key's info");
			keysInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			keysInfoFrame.setVisible(true);
		default:
			break;
		}
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}
