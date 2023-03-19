package main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Define {
	final static Double GRAVITY = 0.098; // 중력가속도 (1/100초)
	final static int GROUND = 250; // 지면 Y좌표
	final static int RUNLOCATION = 100; // 달리는 X좌표

}

public class GraphicsMainDraw extends JFrame {
	// MyPanel 객체 생성
	private MyPanel panel = new MyPanel();
	private int counter, maxscore = 0;

	// 게임 시작 & 오버 메시지
	private String mainMessage = "";
	private String mainMessage2 = "";

	// 더블 버퍼링 변수
	private Image bufferImage;
	private Graphics screenGraphic;

	
	
	// 플레이어 변수
	private int playerX = Define.RUNLOCATION, playerY = Define.GROUND, playerAction = 1; // 좌표 (초기좌표)
	private int playerSizeX = 50, playerSizeY = 50;
	private ImageIcon playerIcon0 = new ImageIcon(getClass().getClassLoader().getResource("run0.png")); // 아이콘 불러오기
	private ImageIcon playerIcon1 = new ImageIcon(getClass().getClassLoader().getResource("run1.png")); // 아이콘 불러오기
	private ImageIcon playerIcon2 = new ImageIcon(getClass().getClassLoader().getResource("run2.png")); // 아이콘 불러오기
	private Image playerImg = playerIcon1.getImage(); // 이미지 지정
	private boolean hit = false, retry = false;

	// 플레이어 키 입력 변수
	private boolean up = false, down = false, releaseUp = true;
	private int vt = 0, vf = 0;

	// 바닥 변수
	private ImageIcon groundIcon = new ImageIcon(getClass().getClassLoader().getResource("floor.png")); // 아이콘 불러오기
	private Image groundImg = groundIcon.getImage(); // 이미지 지정

	// 구름 변수
	private ImageIcon cloudIcon = new ImageIcon(getClass().getClassLoader().getResource("cloud_cut.png")); // 아이콘 불러오기
	private Image cloudImg = cloudIcon.getImage(); // 이미지 지정
	private ArrayList<cloud> cloudList = new ArrayList<>();

	// 선인장 변수
	private ImageIcon cactusIcon1 = new ImageIcon(getClass().getClassLoader().getResource("g1.png")); // 아이콘 불러오기
	private ImageIcon cactusIcon2 = new ImageIcon(getClass().getClassLoader().getResource("g2.png")); // 아이콘 불러오기
	private ImageIcon cactusIcon3 = new ImageIcon(getClass().getClassLoader().getResource("g3.png")); // 아이콘 불러오기
	private ImageIcon cactusIcon4 = new ImageIcon(getClass().getClassLoader().getResource("g4.png")); // 아이콘 불러오기
	private ImageIcon cactusIcon5 = new ImageIcon(getClass().getClassLoader().getResource("bird.png")); // 아이콘 불러오기
	private ArrayList<cactus> cactusList = new ArrayList<>();
	private cactus c1 = new cactus();

	private double accel = 0; // 1/500배

	// 화면크기
	private final int SCREENX = 1000;
	private final int SCREENY = 400;

	// 클래스 :
	// 키 입력 클래스
	public class AL extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == e.VK_DOWN) {
				down = true;
			}
			if (keyCode == e.VK_UP)
				up = true;
			if (keyCode == e.VK_ENTER && retry == false && hit == true)
				retry = true;

		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == e.VK_DOWN) {
				down = false;
			}
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

		while (up == false) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mainMessage = "Dino Game";
			mainMessage2 = "Use 'Up' and 'Down' key";
			
		}
		init();

		// 무한 루프문 :
		// 게임 시나리오 작성 공간
		while (counter < 50 && hit == false) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}

			counter++;

			GenerateProcess(); // 유닛 생성 프로세스
			MoveProcess(); // 유닛 이동 프로세스
			keyProcess(); // 플레이어 이동&공격 프로세스
			removeProcess();
			checkProcess();

		}

		while (true) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}

			accel = accel < 1 ? accel + 0.0005 : accel;
			counter++;

			GenerateProcess(); // 유닛 생성 프로세스
			keyProcess(); // 플레이어 이동
			MoveProcess(); // 유닛 이동 프로세스
			checkProcess();
			removeProcess();

			counter = counter > 99999 ? 99999 : counter; // 카운터 고정

			// 피격된 경우
			// 정지
			while (hit) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mainMessage = "GAVE OVER";
				mainMessage2 = "Press ENTER to continue";

				if (retry) {
					retry = false;
					maxscore = maxscore < counter ? counter : maxscore;
					init();
					hit = false;

				}
			}

		}

	}

	// 메소드 : 초기화
	// 속도 초기화
	// 선인장 초기화
	public void init() {
		// 플레이어 지면으로 이동
		playerY = Define.GROUND;

		// 시작 점프
		vf = 1;
		vt = 0;

		// 시간(점수) 초기화
		counter = 0;

		// 가속 초기화
		accel = 0;

		// 선인장 초기화
		cactusList.clear();
		new cloud(1040, (int) (Math.random() * 71 + 50), cloudImg);
		cactus.ccounter = 160;
		mainMessage = "";
		mainMessage2 = "";

	}

	// 클래스 : 선인장 클래스
	// 선인장 이동시키고, 다음 장애물 생성위치 결정
	class cactus {
		private int x, y, sizeX, sizeY;
		private Image cactusImg;
		private static int nextX, nextY, ccounter, type;

		public cactus() {

		}

		public cactus(int x, int y, int sizeX, int sizeY, Image cactusImg) {
			super();
			this.x = x;
			this.y = y;
			this.sizeX = sizeX;
			this.sizeY = sizeY;
			this.cactusImg = cactusImg;

			cactusList.add(this);

			// 생성위치
			cactus.ccounter = (int) ((Math.random() * 25 + this.sizeX / 2 + 10) * (2 - accel));
			cactus.type = (int) (Math.random() * 7) + 0;
		}

		void move() {
			this.x -= (14 - 7 + (int) (7 * accel));

		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public Image getCactusImg() {
			return cactusImg;
		}

		public int getSizeX() {
			return sizeX;
		}

		public void setSizeX(int sizeX) {
			this.sizeX = sizeX;
		}

		public int getSizeY() {
			return sizeY;
		}

		public void setSizeY(int sizeY) {
			this.sizeY = sizeY;
		}

		public void setCactusImg(Image cactusImg) {
			this.cactusImg = cactusImg;
		}

	}

	// 클래스 : 구름 클래스
	// 구름 이동시키고, 다음 구름 생성위치 마련
	class cloud {
		private double x, y;
		private Image cloudImg;
		private static int nextX, nextY;

		public cloud() {

		}

		public cloud(int x, int y, Image cloudImg) {
			super();
			this.x = x;
			this.y = y;
			this.cloudImg = cloudImg;

			cloudList.add(this);
			nextX = (int) (Math.random() * 200 + 1000);
			nextY = (int) (Math.random() * 81 + 20);
		}

		void move() {
			this.x -= 1.2;

		}

		public int getX() {
			return (int) x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public int getY() {
			return (int) y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public Image getCloudImg() {
			return cloudImg;
		}

		public void setCloudImg(Image cloudImg) {
			this.cloudImg = cloudImg;
		}

	}

	// 메소드:
	// 키 입력값과 비교하여 플레이어 행동 실행
	public void keyProcess() {
		if (up) {
			if (releaseUp) {
				vf = 10;
				releaseUp = false;
				vt = 0;
			}
		}
		if (down) {
			vt = 90;
		}
	}

	// 메소드 :
	// 유닛 생성 메소드
	public void GenerateProcess() {
		// 구름 생성 :
		// 마지막 구름이 x700 좌표를 넘어가면 하나 생성
		if (cloudList.get(cloudList.size() - 1).getX() < 700) {
			new cloud(cloud.nextX, cloud.nextY, cloudImg);
		}

		// 선인장 생성 :
		//
		cactus.ccounter--;
		if (cactus.ccounter <= 0) {
			switch (cactus.type) {
			// 긴 선인장 하나
			case 0: {
				new cactus(1010, 245, 35, 55, cactusIcon1.getImage());
				break;
			}

			// 작은 선인장 하나
			case 1: {
				new cactus(1010, 210, 50, 80, cactusIcon1.getImage());
				break;
			}

			// 중간 선인장 1개
			case 2: {
				new cactus(1010, 230, 45, 70, cactusIcon2.getImage());
				break;
			}

			// 작은 선인장 3개
			case 3: {
				new cactus(1010, 245, 80, 55, cactusIcon3.getImage());
				break;
			}

			// 긴 선인장 여러개
			case 4: {
				new cactus(1010, 220, 110, 80, cactusIcon4.getImage());
				break;
			}
			// 낮은 새
			case 5: {
				new cactus(1010, 245 - 30, 55, 55, cactusIcon5.getImage());
				break;
			}
			// 낮은 새
			case 6: {
				new cactus(1010, 245 - 30 - 60, 50, 55, cactusIcon5.getImage());
				break;
			}

			default:
				throw new IllegalArgumentException("Unexpected value: " + cactus.type);
			}

		}

	}

	// 메소드 :
	// 유닛 이동 프로세스
	public void MoveProcess() {
		/*
		 * System.out.println("Y 좌표 : " + playerY); System.out.println("중력 가속도 : " + vf
		 * + ", vt : " + vt); System.out.println("accel : " + accel + ", cactus 객체 수 : "
		 * + cactusList.size());
		 */

		// 플레이어 점프
		// 최고 높이 : y 184
		if (vf > 0 || playerY < Define.GROUND) {
			vf -= (int) (Define.GRAVITY * vt);
			playerY -= vf;
			if (playerY - (vf - (int) (Define.GRAVITY * (vt + 1))) >= Define.GROUND) {
				playerY = Define.GROUND;
				releaseUp = true;
			}
			vt = vt < 12 ? vt + 1 : vt;

		}

		// 플레이어 달리기 이미지 변경
		if (down) { // 웅크리기
			playerImg = playerIcon0.getImage();
		} else if (playerY < Define.GROUND) { // 점프 동작
			playerImg = playerIcon1.getImage();
		} else if (counter % 4 == 0) {
			if (playerAction == 1) {
				playerImg = playerIcon2.getImage(); // 달리기2
				playerAction = 2;
			} else if (playerAction == 2) {
				playerImg = playerIcon1.getImage(); // 달리기1
				playerAction = 1;
			}
		}

		// 구름 이동
		for (int i = 0; i < cloudList.size(); i++) {
			cloudList.get(i).move();
		}

		// 선인장 이동
		for (int i = 0; i < cactusList.size(); i++) {
			cactusList.get(i).move();
		}
	}

	// 메소드 : 충돌 체크 프로세스
	// 플레이어의 충돌 여부 확인
	public void checkProcess() {
		for (int i = 0; i < cactusList.size(); i++) {
			c1 = cactusList.get(i);
			if (playerX + playerSizeX - 7 > c1.getX() + 5 && playerX + 7 < c1.getX() + c1.getSizeX() - 8
					&& playerY + playerSizeY > c1.getY() + 4 && playerY +4 + downChecker() < c1.getY() + c1.getSizeY()) {
				hit = true;
			}
		}
	}

	// 메소드 : 물체 제거 메소드
	// 화면 나간 물체 제거
	public void removeProcess() {
		for (int i = 0; i < cactusList.size(); i++) {
			if (cactusList.get(i).getX() < -150)
				cactusList.remove(i);
		}
	}

	public int downChecker() {
		if (down) {
			return 25;
		} else {
			return 0;
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
			// 구름 그리기
			for (int i = 0; i < cloudList.size(); i++)
				g.drawImage(cloudImg, cloudList.get(i).getX(), cloudList.get(i).getY(), 60, 25, this);

			// 선인장 그리기
			for (int i = 0; i < cactusList.size(); i++)
				g.drawImage(cactusList.get(i).getCactusImg(), cactusList.get(i).getX(), cactusList.get(i).getY(),
						cactusList.get(i).getSizeX(), cactusList.get(i).getSizeY(), this);

			// 바닥 그리기
			g.drawImage(groundImg, 0, 290, 1000, 11, this);

			// 플레이어 그리기
			g.drawImage(playerImg, playerX, playerY, playerSizeX, playerSizeY, this);

			// 점수 그리기
			g.setFont(new Font("맑은 고딕", getFont().PLAIN, 20));
			g.drawString("score : " + Integer.toString(counter), 770, 20 + 10);
			g.setFont(new Font("맑은 고딕", getFont().PLAIN, 20));
			g.drawString("Max score : " + Integer.toString(maxscore), 550, 20 + 10);
			g.setFont(new Font("맑은 고딕", getFont().BOLD, 35));
			g.drawString(mainMessage, 390, 140);
			g.setFont(new Font("맑은 고딕", getFont().PLAIN, 20));
			g.drawString(mainMessage2, 370, 200);

			repaint();
		}

	}

	// 왜 업데이트가 안되냐
}
