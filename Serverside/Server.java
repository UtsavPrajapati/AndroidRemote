import java.io.*;
import java.net.*;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import java.awt.MouseInfo;

import java.awt.event.InputEvent;

import java.awt.PointerInfo;
import java.awt.Point;

import javax.imageio.ImageIO;

public class Server{
	
	private static BufferedReader reader;
	private static ObjectOutputStream writer;
	private static Robot Potato;
	private static Socket client;
	private static float sens = 2.f;
	
	public static void main(String[] args)
	{
		try{
			Potato = new Robot();
			
			
            
			
			ServerSocket server = new ServerSocket(6666);
			System.out.println("Waiting for Client");
			client = server.accept();
			System.out.println("Client connected");
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new ObjectOutputStream(client.getOutputStream());
			
			ImageSender image_sender = new ImageSender();
			image_sender.start(); //threads run()
			//System.out.println("we are waiting");
			while(true)
			{
				//System.out.println("we are waiting");
				String g = reader.readLine();
				switch(g)
				{
					case "Left":
						
						Potato.mousePress(InputEvent.BUTTON1_DOWN_MASK);
						Potato.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
						break;
					case "Right":
						Potato.mousePress(InputEvent.BUTTON3_DOWN_MASK);
						Potato.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
						break;
					default:
						PointerInfo pt = MouseInfo.getPointerInfo();
						Point pty = pt.getLocation();
						String[] coords = g.split("&&",2);
	
						int x = (int) (pty.getX() + Float.parseFloat(coords[0])*+sens);
						int y = (int) (pty.getY() + Float.parseFloat(coords[1])*+sens);
					
						Potato.mouseMove(x, y);
						break;
					
				}
				//sendimg();
				
			}
		}catch(Exception e)
		{
			System.out.println(e);
			
		}  
	}
	
	static class ImageSender extends Thread
	{
		public void run()
		{
		
			while(client.isConnected())
			{
				try
				{	
					Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
					BufferedImage bufferedImage = Potato.createScreenCapture(rectangle);
					
					BufferedImage cursor = ImageIO.read(new File("c.png"));
					
					int x = MouseInfo.getPointerInfo().getLocation().x;
					int y = MouseInfo.getPointerInfo().getLocation().y;

					Graphics2D graphics2D =bufferedImage.createGraphics();
					graphics2D.drawImage(cursor, x, y, 64, 64, null); 
					
					//System.out.println("Sending Image");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage,"jpg",baos);
					baos.flush();
					byte[] img=baos.toByteArray();
					writer.writeObject(img);
					writer.flush();
				}catch(Exception e)
				{
					System.out.println(e);
					break;
				}  
			}
		}
	}
}