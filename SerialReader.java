import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

public class SerialReader {

    private SerialPort comPort;

    public SerialReader(String portName, int baudRate) {
        comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(baudRate);
        if(!comPort.openPort()) {
            System.out.println("Impossible d'ouvrir le port " + portName);
            System.exit(1);
        }
        System.out.println("Port " + portName + " ouvert avec succès !");
    }

    public void readData() {
        Scanner scanner = new Scanner(comPort.getInputStream());
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println("Données reçues : " + line);
            // Ici tu peux parser la ligne et envoyer vers le cloud
        }
        scanner.close();
    }

    public void close() {
        comPort.closePort();
        System.out.println("Port fermé.");
    }
}
