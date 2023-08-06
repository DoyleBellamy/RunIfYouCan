
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JPanel;

//burasi Cizme Kismi
class PaintPanel extends JPanel {
	int x=19;
	int y=19;
	ArrayList <String >arrayList= new ArrayList<>();

	public void paintComponent(Graphics g) {									
			super.paintComponent(g);
			//yesil kutunun boyanmasi kismi
		      g.setColor(Color.GREEN);
		      g.fillRect(x,y,20,20);
		      //mavi kutularin boyanmasi kismi
		      g.setColor(Color.BLUE);
		      for(String veri:arrayList) {
		    	  String []array=veri.split("/",2);
		    	  g.fillRect(Integer.parseInt(array[0]),Integer.parseInt(array[1]),20,20);
		      }
	}
		   
	   
	public int getIndexOfArrayList(String gelen) {
		  for(int i=0;i<arrayList.size();i++) {
			  if(arrayList.get(i).equals(gelen)) {
				  return i;
			  }	  
		  }
		  return -1;//buraya hicbir zaman girmemeli kod dogru calistigi taktirde
	  }
}

public class Main extends JFrame implements KeyListener{
	Monster yesil;
	PaintPanel panel;
	public class Monster extends Thread{
		ReentrantLock lock = new ReentrantLock();
		int x_konumu;
		int y_konumu;
		int siradaki=0;
		String renk="mavi";
		
		public Monster(int x,int y) {
			x_konumu=x;
			y_konumu=y;
			panel.arrayList.add(this.x_konumu+"/"+this.y_konumu);
		}
		public Monster() {
			renk="yesil";
			x_konumu=240;
			y_konumu=220;
			panel.x=x_konumu;
			panel.y=y_konumu;
		}
		public synchronized void run() {
			//hic bir tusa basilmadan once beklemeleri gerekiyor
			while(yesil.siradaki==0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while(yesil.siradaki>this.siradaki){
				//yesilin hareketi sonrasi ust usteler mi diye kontrol
				if(Math.abs(this.x_konumu-yesil.x_konumu)<20&&Math.abs(this.y_konumu-yesil.y_konumu)<20) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);
				}
				lock.lock();
				
				int degisecekIndex=panel.getIndexOfArrayList(this.x_konumu+"/"+this.y_konumu);
				
				//normal sartlar altinda buraya hicbir zaman girmemeli ama outOfBoundsException yerine bu konuldu
				if(degisecekIndex==-1) {
					degisecekIndex=1;
				}
				//BURADA mavi rectangel 'larin hareketi tanimlanacak
				if(Math.abs(this.x_konumu-yesil.x_konumu)>20) {
					if(yesil.x_konumu>this.x_konumu) {
						this.x_konumu+=10;
					}
					else this.x_konumu-=10;
				}
				else if(Math.abs(this.y_konumu-yesil.y_konumu)>20) {
					if(yesil.y_konumu>this.y_konumu) {
						this.y_konumu+=10;
					}
					else this.y_konumu-=10;
				}
				panel.arrayList.set(degisecekIndex, (this.x_konumu)+"/"+(this.y_konumu));
				this.siradaki++;//hareket tamamlandigi icin siradaki 1 adet artar
				repaint();
				
				//Mavi kutunun hareketi sonrasi olusabilecek temasin kontrolu
				if(Math.abs(this.x_konumu-yesil.x_konumu)<20&&Math.abs(this.y_konumu-yesil.y_konumu)<20) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
				
				lock.unlock();
				//dahaca oyun bitmemisse ve hamlelerini de tamamamlam�slarsa sonraki tusun basilmasini beklesinler 
				while(this.siradaki==yesil.siradaki) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public Main() {
		addKeyListener(this);
		setSize(600,600);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel= new PaintPanel();
		panel.setBackground(Color.RED);
		add(panel,BorderLayout.CENTER);
		this.yesil= new Monster();
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
		int yon= e.getKeyCode();
		if(yon==65&&yesil.x_konumu>5) {
			yesil.x_konumu-=10;
			panel.x=yesil.x_konumu;
			yesil.siradaki++;
		}
		else if(yon==68&&yesil.x_konumu<462) {
			yesil.x_konumu+=10;
			panel.x=yesil.x_konumu;
			yesil.siradaki++;
		}
		else if(yon==87&&yesil.y_konumu>5) {
			yesil.y_konumu-=10;
			panel.y=yesil.y_konumu;
			yesil.siradaki++;
		}
		else if(yon==83&&yesil.y_konumu<440) {
			yesil.y_konumu+=10;
			panel.y=yesil.y_konumu;
			yesil.siradaki++;
		}
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	public static void main(String[] args) {
		//int number_of_monsters = Integer.parseInt(args[0]);

		//Buradan canavar sayısını değiştirebilirsin
		int number_of_monsters = Integer.parseInt("10");
		System.out.println(number_of_monsters);
		Main m = new Main();
		
		Main.Monster [] monsters = new Main.Monster[number_of_monsters];
		
		Random r = new Random();
		
		for(int i=0;i<number_of_monsters;i++)
		{
			monsters[i] = m.new Monster(Math.abs(r.nextInt()%500),Math.abs(r.nextInt()%500));		
		}
		
		for(int i=0;i<number_of_monsters;i++)
			monsters[i].start();
		
		try {
			for(int i=0;i<number_of_monsters;i++)
				monsters[i].join();//bunun bitmesini sonraki icin bekletir sonra digeri devam eder
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
