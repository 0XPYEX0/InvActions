package me.xpyex.lib.xplib.util.version;

import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import me.xpyex.lib.xplib.api.Version;
import me.xpyex.lib.xplib.bukkit.config.GsonUtil;
import me.xpyex.lib.xplib.util.RootUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import org.bukkit.plugin.Plugin;

public class UpdateUtil extends RootUtil {

    public static String getUpdateFromGitHub(Plugin plugin) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/0XPYEX0/" + plugin.getName() + "/releases/latest").openConnection();

            connection.setReadTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new IOException("连接被服务器拒绝，访问码: " + connection.getResponseCode());
            }
            JsonObject result = GsonUtil.parseJsonObject(new String(readInputStream(connection.getInputStream()), StandardCharsets.UTF_8));
            connection.disconnect();
            String newVer = result.get("tag_name").getAsString().toLowerCase().replace("v", "");
            if (new Version(newVer).compareTo(new Version(plugin.getDescription().getVersion())) > 0) {
                return newVer;
            }
        } catch (IOException e) {
            InvActions.getInstance().getLogger().warning("插件 " + plugin.getName() + " 从 GitHub 获取更新失败");
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream ba = new ByteArrayOutputStream(16384)) {
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                ba.write(data, 0, nRead);
            }
            return ba.toByteArray();
        }
    }
}
