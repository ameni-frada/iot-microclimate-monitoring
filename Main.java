import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fazecast.jSerialComm.SerialPort;

public class Main {
    public static void main(String[] args) {
        SerialPort comPort = SerialPort.getCommPort("COM3"); // adapte selon ton port
        comPort.setBaudRate(9600);

        if (comPort.openPort()) {
            System.out.println("✅ Port ouvert avec succès !");
        } else {
            System.out.println("❌ Impossible d'ouvrir le port.");
            return;
        }

        try {
            while (true) {
                if (comPort.bytesAvailable() > 0) {
                    byte[] buffer = new byte[comPort.bytesAvailable()];
                    int numRead = comPort.readBytes(buffer, buffer.length);
                    String data = new String(buffer, 0, numRead).trim();

                    if (!data.isEmpty()) {
                        System.out.println("📡 Données reçues : " + data);

                        // Exemple STM32 : "T=24.5;H=60.2;L=450.1;S=55.8"
                        String[] parts = data.split(";");
                        String json = "{";

                        for (int i = 0; i < parts.length; i++) {
                            String[] kv = parts[i].split("=");
                            if (kv.length == 2) {
                                String key = kv[0].trim();
                                String value = kv[1].trim();
                                json += "\"" + key + "\":" + value;
                                if (i < parts.length - 1) json += ",";
                            }
                        }
                        json += "}";

                        sendToNodeRED(json);
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comPort.closePort();
            System.out.println("🔒 Port fermé.");
        }
    }

    private static void sendToNodeRED(String jsonData) {
        try {
            URL url = new URL("http://127.0.0.1:1880/microclimate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Réponse Node-RED : " + responseCode);
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Erreur envoi HTTP : " + e.getMessage());
        }
    }
}
