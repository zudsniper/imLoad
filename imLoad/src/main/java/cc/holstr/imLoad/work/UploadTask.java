package cc.holstr.imLoad.work;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import cc.holstr.imLoad.gui.Window;
import cc.holstr.imLoad.http.ImgurHttpHelper;
import cc.holstr.imLoad.json.JsonGenerator;

public class UploadTask extends SwingWorker<Void, Void>{

	private List<File> files;
	
	private DefaultListModel<String> str;
	
	private ImgurHttpHelper helper;
	
	private Clipboard clipboard;
	
	public UploadTask(String key, List<File> files, DefaultListModel<String> str, Clipboard clipboard) {
		this.files = files;
		this.str = str;
		this.clipboard = clipboard;
		if(key.equals(Window.apiKeyText)) {
		helper = new ImgurHttpHelper("57d9fa6227f7442");
		} else {
			helper = new ImgurHttpHelper(key);
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		for(File file : files) {
			if(checkIfImage(file)) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					String t = file.getAbsolutePath();
					String ext = t.substring(t.lastIndexOf(".")+1,t.length());
					ImageIO.write(ImageIO.read(file), ext, baos);
					String url = "https://api.imgur.com/3/upload.json";
					Base64.encodeBase64String(baos.toByteArray()).toString();
					String json = JsonGenerator.getUploadImageJson(Base64.encodeBase64String(baos.toByteArray()).toString()).toString();
					InputStream stream = helper.post(url, json);
					String temp = IOUtils.toString(stream, "UTF-8");
					System.out.println(temp);
					temp = temp.substring(temp.indexOf("\"id\"")+6);
					String id = temp.substring(0, temp.indexOf("\",\""));
					String photoUrl = "http://imgur.com/"+id;
					this.str.addElement(photoUrl);
					clipboard.setContents(new StringSelection(photoUrl),null);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} 
		}
		return null;
	}
	
	public boolean checkIfImage(File f) {
		boolean ret = false;
		//stolen off stackOverflow's Ismael
        String mimetype= new MimetypesFileTypeMap().getContentType(f);
       // System.out.println("\n\n"+mimetype);
        String type = mimetype.split("/")[0];
        if(type.equals("image")){
        		return true;
        } else if(mimetype.equals("application/octet-stream")) {
			if(f.getName().contains(".png") || f.getName().contains(".PNG")) {
        		return true; 
        	} 
		}
        return ret;
	}

}
