

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringEscapeUtils;

public class LyricsFetcher {

	public String readUrl(String urlStr) {

		StringBuilder result = new StringBuilder();
		InputStream inputStream = null;
		Reader reader = null;
		try {
			URL url = new URL(urlStr);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setConnectTimeout(10000);
			urlConnection.setReadTimeout(10000);
			urlConnection.connect();
			inputStream = urlConnection.getInputStream();
			reader = new InputStreamReader(inputStream);
			int c = 0;
			char ch = 0;
			while (c != -1) {
				c = reader.read();
				ch = (char) c;
				result.append(ch);
			}
			result.append(System.getProperty("line.separator"));    
		}catch(MalformedURLException me) {
			me.printStackTrace();
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			try{
				if(reader!=null){
					reader.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			}catch(IOException io){
				io.printStackTrace();}
		}
		return result.toString();
	}

	private String getUrlFromXml(String content) {
		String[] parts = content.split("<url>");
		parts = parts[1].split("</url>");
		return parts[0];
	}

	private String fixupInput(String input) {
		return input.replaceAll(" ", "_");
	}

	private String extractLyricsFromPage(String content) {
		StringBuilder result = new StringBuilder();
		String[] parts = content.split("<div class='lyricbox'>");
		if (parts.length < 2) {
			return null;
		}
		parts = parts[1].split("</script>");
		parts = parts[1].split("<!--");

		parts = parts[0].split("<br />");
		for (String part: parts) {
			result.append(StringEscapeUtils.unescapeHtml4(part));
			result.append(System.getProperty("line.separator"));

		}
		return result.toString();
	}

	public String fetchLyrics(String artist, String title) {
		// need to call out and get url  http://lyrics.wikia.com/api.php?func=getSong&artist=Tool&song=Schism&fmt=xml
		String url = "http://lyrics.wikia.com/api.php?func=getSong&artist=" + fixupInput(artist) + 
				"&song=" + fixupInput(title) + "&fmt=xml";
		// call real url
		String content = readUrl(url);
		url = getUrlFromXml(content);
		content = readUrl(url);
		String lyrics = extractLyricsFromPage(content);
		return lyrics;
	}

}
