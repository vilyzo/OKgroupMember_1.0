import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class okGroupMember {
    public static void main(String[] args) {
        System.out.println("@harmesm");

        System.out.println("Введите (application_key)");
        Scanner tokenScan = new Scanner(System.in);
        String application_key = tokenScan.nextLine();
        System.out.println("Введите (access_token)");
        String access_token = tokenScan.nextLine();
        System.out.println("Введите (session_secret_key)");
        String session_secret_key = tokenScan.nextLine();

        try {
            //читаем файл
            Scanner scanner = new Scanner(new File("list.txt"));
            while (scanner.hasNextLine()) {
                String tmp = scanner.nextLine();
                String pubListSig = tmp;

                // Создаем URL-адрес для запроса
                URL url = new URL("https://api.ok.ru/fb.do?");

                // Создаем объект HttpURLConnection и настраиваем его
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                //Создаем sig
                String sigGen = "application_key=" + application_key + "format=jsonmethod=url.getInfourl=" + pubListSig + session_secret_key;
                //System.out.println("sig готов " + sigGen);

                //хешируем sig
                java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
                byte[] md5_byte_array = md5.digest(sigGen.getBytes());
                String md5_string = new String(md5_byte_array);
                //System.out.println(md5_string);

                // Создаем тело запроса
                String body = "application_key=" + application_key + "&format=json&method=url.getInfo&url="+ pubListSig +"&sig=" + md5_string + "&access_token=" + access_token;
                //System.out.println("Тело запроса " + body);

                // Записываем тело запроса в поток вывода
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();

                // Считываем ответ от сервера
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Выводим ответ от сервера
                String groupId = response.substring(27, response.indexOf("}"));
                //System.out.println("groupID " + groupId);

                //---------------Узнаем количество подписчиков-------------------

                // Создаем URL-адрес для запроса
                url = new URL("https://api.ok.ru/fb.do?");

                // Создаем объект HttpURLConnection и настраиваем его
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                //Создаем sig
                String sigGenMember = "application_key=" + application_key + "counterTypes=membersformat=jsongroup_id=" + groupId + "method=group.getCounters" + session_secret_key;
                //System.out.println("sig готов " + sigGenMember);

                //хешируем sig
                md5_byte_array = md5.digest(sigGenMember.getBytes());
                md5_string = new String(md5_byte_array);


                // Создаем тело запроса
                String bodyMember = "application_key=" + application_key + "&counterTypes=members&format=json&group_id=" + groupId + "&method=group.getCounters&sig=" + md5_string + "&access_token=" + access_token;
                //System.out.println("Тело запроса " + bodyMember);

                // Записываем тело запроса в поток вывода
                writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(bodyMember);
                writer.flush();

                // Считываем ответ от сервера
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String lineMember;
                StringBuilder responseMember = new StringBuilder();
                while ((lineMember = reader.readLine()) != null) {
                    responseMember.append(lineMember);
                }
                reader.close();

                // Выводим ответ от сервера
                String groupMember = responseMember.substring(23, responseMember.indexOf("}"));
                System.out.printf("%-40s%10s%n", tmp, groupMember);
            }
            scanner.close();
            System.out.println("Готово");
            int c = System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}