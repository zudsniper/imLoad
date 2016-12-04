package cc.holstr.imLoad.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cc.holstr.imLoad.gui.model.PicturePanel;
import cc.holstr.imLoad.load.ResourceLoader;
import cc.holstr.imLoad.unpack.Unpacker;
import cc.holstr.imLoad.work.UploadTask;

public class Window extends JFrame implements ListSelectionListener{
	
	private Clipboard clipboard;
	
	private JPanel layout;
	private PicturePanel drop;
	
	private JTextField apikey;
	private JButton copyAll; 
	
	private JLabel dropHere;
	
	private DefaultListModel<String> model;
	private JList history;
	private JScrollPane scroll;
	
	public static final String apiKeyText = "API Key (optional)";
	
	public Window() {
		build();
	}
	
	public void build() {
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		layout = new JPanel(new BorderLayout());
		drop = new PicturePanel(ResourceLoader.toBufferedImage(ResourceLoader.getImageFromResources("gradient.png")));
		dropHere = new JLabel("Drop Image Here");
		apikey = new JTextField(apiKeyText);
		copyAll = new JButton("Copy All");
		model = new DefaultListModel<String>();
		history = new JList(model);
		scroll = new JScrollPane(history);
		
		JPanel bottom = new JPanel(new BorderLayout());
		
		history.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		history.addListSelectionListener(this);
		
		copyAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allToClip();
			}
		});
		
		apikey.setForeground(Color.GRAY);
		apikey.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(apikey.getText().equals("API Key...")) {
					apikey.setForeground(Color.BLACK);
					apikey.setText("");
				}
			}
			
			public void focusLost(FocusEvent e) {
				if(apikey.getText().equals("")) {
					apikey.setForeground(Color.GRAY);
					apikey.setText("API Key...");
				}
			}
		});
		
		drop.setDropTarget(new DropTarget() {
	        public synchronized void drop(DropTargetDropEvent evt) {
	            try {
	                evt.acceptDrop(DnDConstants.ACTION_COPY);
	                List<File> droppedFiles = (List<File>) evt
	                        .getTransferable().getTransferData(
	                                DataFlavor.javaFileListFlavor);
	                System.out.println("[imLoad] file dropped...");
	               upload(droppedFiles);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
		
		
		bottom.add(scroll, BorderLayout.CENTER);
		bottom.add(copyAll, BorderLayout.SOUTH);
		
		drop.add(dropHere);
		layout.add(apikey,BorderLayout.NORTH);
		layout.add(drop,BorderLayout.CENTER);
		layout.add(bottom, BorderLayout.SOUTH);
		add(layout);
		
		addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	if(!apikey.getText().equals(apiKeyText)) {
			    	Unpacker.writeAPIKey(apikey.getText());
		    	}
			    System.exit(0);
		    	
		    }
		});
		
		if(Unpacker.apikey!=null) {
			apikey.setForeground(Color.BLACK);
			apikey.setText(Unpacker.apikey);
		}
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(240,300));
		setResizable(false);
	}
	
	public void upload(List<File> files) {
		new UploadTask(apikey.getText(),files, model,clipboard).execute();
	}

	public void allToClip() {
		String copies = model.getElementAt(0);
		for(int i =1; i<model.getSize();i++) {
			copies += ", " + model.getElementAt(i);
		}
		clipboard.setContents(new StringSelection(copies), null);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		StringSelection stringSelection = new StringSelection((String)history.getSelectedValue());
		clipboard.setContents(stringSelection, null);
	}
	
}
