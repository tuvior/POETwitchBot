package TwitchBot.poe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static TwitchBot.jsonutil.JSONUtil.readJsonFromUrl;

public class BuildTree {

    static String lastName = "";
    static int treeSize = 0;

    static String lastUrl = "";


    public static String saveToURL(int [] tree, int class_) {
        byte[] b = new byte[(tree.length) * 2];
        String CharacterURL = getCharacterURL((byte)class_);
        int pos = 0;
        for (int inn : tree)
        {
            byte[] dbff = GetBytes(inn);
            b[pos++] = dbff[1];
            b[pos++] = dbff[0];
        }
        String base_string =  new sun.misc.BASE64Encoder().encode(b);
        return "https://www.pathofexile.com/fullscreen-passive-skill-tree/" + CharacterURL + base_string.replace("/", "_").replace("+", "-").replace("\n", "").replace("\r", "");
    }

    public static String loadTree(String name, String account) throws IOException {
        JSONObject treeInfo = readJsonFromUrl("https://www.pathofexile.com/character-window/get-passive-skills?accountName="+ account +"&character=" + name);
        JSONArray ashes = treeInfo.getJSONArray("hashes");

        JSONObject character = readJsonFromUrl("https://www.pathofexile.com/character-window/get-items?character=" + name + "&accountName=" + account);
        int class_ = character.getJSONObject("character").getInt("class");

        int [] hashes = new int [ashes.length()];

        for (int i = 0; i < ashes.length(); i++) {
            hashes[i] =ashes.getInt(i);
        }

        if (name.equals(lastName) && hashes.length == treeSize) {
            return lastUrl;
        }

        lastName = name;
        treeSize = hashes.length;

        String treeUrl = saveToURL(hashes, class_);

        JSONObject result = readJsonFromUrl("http://poeurl.com/api/?shrink={%22url%22:%22" + treeUrl + "%22}");

        lastUrl = "poeurl.com/" + result.getString("url");

        return lastUrl;
    }

    public static String getCharacterURL(byte CharTypeByte) {
        byte[] b = new byte[6];
        byte[] b2 = GetBytes(3);
        for (int i = 0; i < b2.length; i++) {
            b[i] = b2[(b2.length - 1) - i];
        }
        b[4] = CharTypeByte;
        b[5] = 0;
        String base_string = new sun.misc.BASE64Encoder().encode(b);
        return base_string.replace("/", "_").replace("+", "-");
    }

    public static byte[] GetBytes(int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
        buffer.putInt(value);
        return buffer.array();
    }
}
