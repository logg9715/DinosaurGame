package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Define {
	final static Double GRAVITY = 0.098;	// 중력가속도 (1/100초)
	final static int GROUND = 360;			// 지면 Y좌표
	final static int RUNLOCATION = 160;		// 달리는 X좌표

}

public class GraphicsMainDraw extends JFrame {
	// MyPanel 객체 생성
	private MyPanel panel = new MyPanel();
	private int counter;

	// 더블 버퍼링 변수
	private Image bufferImage;
	private Graphics screenGraphic;

	// 플레이어 변수
	private int playerX = Define.RUNLOCATION, playerY = Define.GROUND; // 좌표 (초기좌표)
	private ImageIcon playerIcon = new ImageIcon("images/playerImg.png"); // 아이콘 불러오기
	private Image playerImg = playerIcon.getImage(); // 이미지 지정
	// 플레이어 키 입력 변수
	private boolean up = false, down = false, releaseUp = true;
	private int vt = 0, vf = 0;

	// 화면크기
	private final int SCREENX = 1000;
	private final int SCREENY = 600;

	// 클래스 :
	// 키 입력 클래스
	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == e.VK_DOWN)
				down = true;
			if (keyCode == e.VK_UP)
				up = true;

		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == e.VK_DOWN)
				down = false;
			if (keyCode == e.VK_UP) {
				up = false;
			}
		}
	}

	// 기본 생성자 :
	public GraphicsMainDraw() {
		addKeyListener(new AL());
		setTitle("공룡 달리기 게임");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(panel);
		setResizable(false);
		setSize(SCREENX, SCREENY); // 화면 크기 지정
		setVisible(true);
		setLocationRelativeTo(null);

		// 무한 루프문 :
		// 게임 시나리오 작성 공간
		while (true) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}

			counter++;

			keyProcess(); // 플레이어 이동&공격 프로세스
			MoveProcess();

			counter = counter > 99999999 ? 0 : counter; // 카운터 초기화

		}
	}

	// 메소드:
	// 키 입력값과 비교하여 플레이어 행동 실행
	public void keyProcess() {
		if (up) {
			if (releaseUp) {
				vf = 11;
				releaseUp = false;
				vt = 0;
			}
		}
	}

	// 메소드 :
	// 유닛 이동 프로세스
	public void MoveProcess() {
		System.out.println("Y 좌표 : " + playerY);
		System.out.println("중력 가속도 : " + vf);

		// 플레이어 점프
		// 최고 높이 : y 184
		if (vf > 0 || playerY < Define.GROUND) {
			vf -= (int)(Define.GRAVITY * vt);
			playerY -= vf;
			if (playerY - (vf - (int)(Define.GRAVITY * (vt + 1))) > Define.GROUND) {
				playerY = Define.GROUND;
				releaseUp = true;
			}
			vt = vt < 12 ? vt + 1 : vt;
			
		}
	}

	// 클래스 :
	// 패널 생성 클래스
	class MyPanel extends JPanel {
		// 메소드 :
		// 페인트
		public void paint(Graphics g) {
			bufferImage = createImage(getWidth(), getHeight());
			screenGraphic = bufferImage.getGraphics();
			paintComponent(screenGraphic);
			g.drawImage(bufferImage, 0, 0, null);
		}

		// 메소드 : 더블버퍼 그리기
		// 유닛들 여기에 추가
		public void paintComponent(Graphics g) {
			// 플레이어 그리기
			g.drawImage(playerImg, playerX, playerY, 60, 60, this);

			repaint();
		}

	}

	// 왜 업데이트가 안되냐
}
