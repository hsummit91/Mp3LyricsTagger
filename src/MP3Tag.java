import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;


public class MP3Tag {
	private AudioFile mp3;
	private String filePath;
	private String[] extensions = {"mp3", "ogg", "mp2", "m4a"};
	public void readFile(){
		try{	
			StringBuilder sb = new StringBuilder();
			for(String str : extensions){
				sb.append(str+", ");
			}

			String ext = sb.toString();
			ext = ext.substring(0, ext.length()-2);

			JFileChooser fileopen = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter("Music Files ("+ext+")", extensions);
			fileopen.setFileHidingEnabled(true);
			fileopen.addChoosableFileFilter(filter);

			int ret = fileopen.showDialog(null, "Open file");

			if (ret == JFileChooser.APPROVE_OPTION) {
				filePath = fileopen.getSelectedFile().getAbsolutePath();
			} else{
				System.exit(0);
			}

			mp3 = AudioFileIO.read((new File(filePath)));
			Tag tag = mp3.getTag();
			String lyric = (new LyricsFetcher().fetchLyrics(tag.getFirst(FieldKey.ARTIST), tag.getFirst(FieldKey.TITLE)));
			tag.setField(FieldKey.LYRICS, lyric.trim());
			System.err.println(tag.getFirst(FieldKey.ARTIST)+" | "+tag.getFirst(FieldKey.TITLE)+"\n");
			System.out.println(tag.getFirst(FieldKey.LYRICS));
			mp3.commit();
		}catch(CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotWriteException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
