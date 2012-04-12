package myGame.doodleTetris;

import java.util.Random;

import myGame.doodleTetris.Block.BlockType;
import myGame.doodleTetris.framework.AndroidGraphics;
import myGame.doodleTetris.framework.Game;
import myGame.doodleTetris.framework.Music;
import myGame.doodleTetris.framework.Screen;
import myGame.doodleTetris.framework.SingleTouch;

public class ClassicGameScreen extends Screen {

	boolean isMusic = true;
	boolean isSound = true;
	String nameMusic[]={"bg_track","bg_track","bg_track"};
	Music music;
	public void setupButton () {
		// set game button arrow
		Asset.btn_left.setPosition(48, 696);
		Asset.btn_right.setPosition(216, 696);
		Asset.btn_rotate.setPosition(360, 696);
		Asset.btn_down.setPosition(360, 600);
		Asset.btn_pause.setPosition(384, 72);
		
		// set menu button
		Asset.btn_mainMenu.setPosition(48, 312);
		Asset.btn_playAgain.setPosition(48, 432);
		
		// set icon
		Asset.icon_boomb.setPosition(384,360);
		Asset.icon_dynamite.setPosition(384,360+72);
		Asset.icon_rocket.setPosition(384,360+72+72);
		
		Asset.icon_music.setPosition(360,24);
		Asset.icon_sound.setPosition(360+48,24);
		
		Random r = new Random ();
		Asset.bg_gameScreen = Asset.list_bg[r.nextInt(Asset.list_bg.length)];
		
		
	}
	
	enum GameState {
		Ready,
		Running,
		Paused,
		StageClean,
		GameOver,
		Exit,
	}
	
	GameState gameState = GameState.Running;
	Board board;
	int currentScore;
	int tempScore=0;
	String strScore="0";
	int level=1;
	int time=0;
	String minus,second;
	public ClassicGameScreen(Game game) {
		super(game);
		board = new Board();
		setupButton ();
		// TODO Auto-generated constructor stub
	//	play track
//		Asset.bg_track.play ();
		//Duong update music 12/4/2012
		Random r = new Random();
		int resid = game.getContext().getResources().getIdentifier(nameMusic[r.nextInt(nameMusic.length)], "raw", game.getContext().getPackageName());
		music = new Music(game.getContext(), resid);
		music.play();
	}
	
	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		if (gameState == GameState.Running)
			updateRunning(deltaTime);
		if (gameState == GameState.Paused)
			updatePause();
		if (gameState == GameState.GameOver)
			updateGameOver();
		if (gameState == GameState.StageClean)
			updateStageClean();
		if (gameState == GameState.Exit)
			updateExit();
		
	}
	
	// int bonus image duration present
	int bonusDuration = 250000000;
	int startDrawBonus =-1;
	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
	AndroidGraphics g = game.getGraphics();
	g.drawImage(Asset.bg_gameScreen);
	g.drawImage(Asset.bg_board.bitmap, (int )Asset.bg_board.x,(int ) Asset.bg_board.y);
	g.drawImage(Asset.btn_left);
	g.drawImage(Asset.btn_right);
	g.drawImage(Asset.btn_rotate);
	g.drawImage(Asset.btn_down);
	g.drawImage(Asset.btn_pause);
	// icon
	g.drawImage(Asset.icon_boomb);
	g.drawImage(Asset.icon_dynamite);
	g.drawImage(Asset.icon_rocket);
	
	//
	g.drawImage(Asset.icon_music);
	g.drawImage(Asset.icon_sound);

	drawScore(strScore,360,240);
	drawLevel( level);
	drawTime (time,360,312);

	if (!timeExpired(startDrawBonus, bonusDuration)){
		drawBonus(board.getBonus());
	}
	drawBoard(board);
	// ve current Block
	drawBlock (board.currentBlock);
	drawBlock (board.nextBlock);
	
	if (gameState == GameState.Paused )
		{drawPauseUI(); }
	
	if (gameState == GameState.GameOver )
	{drawGameOverUI();}
	if (gameState == GameState.StageClean )
	{drawStageCleanUI();}
	
	if (gameState == GameState.Exit )
		{drawExitUI();}
	
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if (gameState == GameState.Running)
			gameState = GameState.Exit;
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	void drawBoard (Board board) {
		int unit_cell = Block.UNIT_CELL;
		
		// ve board
		for (int i=0;i<Board.BOARD_WIDTH;i++){
			for (int j=4;j<Board.BOARD_HEIGHT;j++) {
				drawCell(board.map[i][j],(i+1)*unit_cell,(j-4)*unit_cell);
				}// for j
			}//for i
		
		
	}
	
	// ve Block ()
	void drawBlock (Block block) {
		int unit_cell = Block.UNIT_CELL;
		for (int i=0;i<Block.BLOCK_HEIGHT;i++){
			for (int j=0;j<Block.BLOCK_HEIGHT;j++) {
				if (block.status[i][j])
				drawCell(block.type,(block.x+i+1)*unit_cell,(block.y+j-4)*unit_cell);
				}// for j
			}//for i
	}
	
	// ve cell
	public void drawCell(BlockType blocktype, int x, int y){
		AndroidGraphics g = game.getGraphics();
			
		switch (blocktype){
		case RED:
			g.drawImage( Asset.block_red.bitmap,x,y);
			break;
		case BLUE:
			g.drawImage(Asset.block_blue.bitmap,x,y);
			break;
		case GREEN:
			g.drawImage(Asset.block_green.bitmap,x,y);
			break;
		case WHITE:
			g.drawImage(Asset.block_white.bitmap,x,y);
			break;
		case YELLOW:
			g.drawImage(Asset.block_yellow.bitmap,x,y);
			break;
		case ORANGE:
			g.drawImage(Asset.block_orange.bitmap,x,y);
			break;
		case PURPLE:
			g.drawImage(Asset.block_purple.bitmap,x,y);
			break;
		case BOOMB:
			g.drawImage(Asset.item_boomb.bitmap,x,y);
			break;
		case NULL:
			g.drawImage(Asset.block_null.bitmap,x,y);
			break;
			//Duong insert case MAP
		case MAP:
			g.drawImage(Asset.block_map.bitmap,x,y);
			break;
		}// switch case
	}
	

	
	
	//ve level
	public void drawLevel(int level){
		AndroidGraphics g = game.getGraphics();
		g.drawImage(Asset.numberLevel[level].bitmap,24,168);
	}
	
	// ve so
	public void drawNumber(Character c, int x, int y){
		
		AndroidGraphics g = game.getGraphics();
			g.drawImage( Asset.number[(int)c-48].bitmap,x,y);
		}
	
	// ve bonus
	public void drawBonus(int bonus){
		AndroidGraphics g = game.getGraphics();
		if (bonus == -1) return;
		g.drawImage(Asset.bonusImg[bonus].bitmap,72,312);
	}
	
	int start = (int) System.nanoTime();
	void updateRunning (float deltaTime){
		SingleTouch TouchEvent = game.getTouchEvent();
		
		
		int duration = 80000000;
		if (timeExpired(start,duration) )	{
			start = (int)System.nanoTime();
			
			if ( Asset.btn_rotate.isTouchDown(TouchEvent)  )
				{ board.currentBlock.rotation(board); 		 Asset.sound_move.play(); }
				
			
			if ( Asset.btn_left.isTouchDown(TouchEvent)   )
			{	board.currentBlock.goLeft(board);	 Asset.sound_move.play(); }
			
			if ( Asset.btn_right.isTouchDown(TouchEvent)   )
				{board.currentBlock.goRight(board); 	 Asset.sound_move.play(); }
			}
	
		if ( Asset.btn_down.isTouchDown(TouchEvent)   )
			board.currentBlock.goDown(board);
		
		AndroidGraphics g = game.getGraphics();
		// check touch item
		if ( Block.item ==-1){
		if ( Asset.icon_boomb.isTouch(TouchEvent) )
			{Block.item = 7;Asset.icon_boomb.setPosition(-5000, -5000);}
		if ( Asset.icon_dynamite.isTouch(TouchEvent)   )
			{Block.item = 8;Asset.icon_dynamite.setPosition(-5000, -5000);}
		if ( Asset.icon_rocket.isTouch(TouchEvent)   )
			{Block.item = 9;Asset.icon_rocket.setPosition(-5000, -5000);}
		}
		
		if ( Asset.icon_music.isTouchDown(TouchEvent)   )
		{
			if (isMusic){
				Asset.icon_music.setBitmap(g.newBitmap("Button/icon_music_dis.png"));
				music.offVolume();
			}
			else{
				Asset.icon_music.setBitmap(g.newBitmap("Button/icon_music.png"));
				music.onVolume();
			}
			
			isMusic = !isMusic;
		}
	
		if ( Asset.icon_sound.isTouch(TouchEvent)   ){
			if (isSound)
			{
				Asset.icon_sound.setBitmap(g.newBitmap("Button/icon_sound_dis.png"));
		//		Sound.unloadSound();
			}
			else 
			{
				Asset.icon_sound.setBitmap(g.newBitmap("Button/icon_sound.png"));
		//		Sound.setResSoundID(myGame.doodleTetris.R.raw.endfall);
		//		Sound.loadSound(AndroidGame.getContext());
			}
		
			isSound= !isSound;
		}
		
		if (Asset.btn_pause.isTouch(TouchEvent)) {gameState = GameState.Paused; return;}
		if (board.gameOver) { strScore = ""+currentScore; Asset.sound_gameOver.play();   gameState = GameState.GameOver; return;}

		board.update(deltaTime);
		
		
		if (currentScore != board.score){
			// kiem tra level
			if (level != board.level ) { Asset.sound_nextLevel.play();  level = board.level;}
			else Asset.sound_cleanRow.play();
			currentScore = board.score;
		}
		
		if (tempScore<currentScore-5) { tempScore+=5; strScore = "" + tempScore;}
		else { tempScore=currentScore; strScore = "" + tempScore;}
		
		
		// finis all
		time++;
	}
	
	// kiem tra trang thai on pause
	void updatePause (){
		SingleTouch TouchEvent = game.getTouchEvent();
		Asset.bg_track.pause();
		if ( Asset.UI_Pause.isTouch(TouchEvent)   ){
			gameState = GameState.Running;
	//			Music.startMusic();
		}
		// kiem tra xac nhan bam
		if ( Asset.btn_mainMenu.isTouch(TouchEvent)   )
			game.setScreen(new MainMenu(game));
		if ( Asset.btn_playAgain.isTouch(TouchEvent)   )
			game.setScreen(new ClassicGameScreen(game));
	}

	// kiem tra trang thai on game over
	void updateGameOver (){
		SingleTouch TouchEvent = game.getTouchEvent();
		
		if ( Asset.UI_GameOver.isTouch(TouchEvent)   )
			game.setScreen(new MainMenu(game));
		if ( Asset.btn_mainMenu.isTouch(TouchEvent)   )
			game.setScreen(new MainMenu(game));
		if ( Asset.btn_playAgain.isTouch(TouchEvent)   )
			game.setScreen(new ClassicGameScreen(game));
	}
	
	// stage Clean mode
	public void updateStageClean () {
	}
	
	public void updateExit () {
		gameState  = GameState.Exit;
		SingleTouch TouchEvent = game.getTouchEvent();
		{
		if (Asset.btn_yes.isTouch(TouchEvent)){
			
			System.gc();
			// try to kill this process
			android.os.Process.killProcess(game.getTaskId());
			System.exit(0);
			}
			else if (Asset.btn_no.isTouch(TouchEvent))  {
				gameState  = GameState.Paused;
			}
		}
	}
	
	// ve pause
	void drawPauseUI (){
		AndroidGraphics g = game.getGraphics();
		g.drawImage(Asset.UI_Pause);
		g.drawImage(Asset.btn_mainMenu);
		g.drawImage(Asset.btn_playAgain);
		
	}
	
	// ve game ogber
	void drawGameOverUI (){
		AndroidGraphics g = game.getGraphics();
		g.drawImage(Asset.UI_GameOver);
		g.drawImage(Asset.btn_mainMenu);
		g.drawImage(Asset.btn_playAgain);
		drawStringNumber (""+currentScore, 84,192);
		drawTime (time, 192, 192);
		
	}
	//StaeClean mode;
	
	void drawStageCleanUI (){
	
	}
	void drawExitUI (){
		Asset.btn_yes.setPosition(144, 288);
		Asset.btn_no.setPosition(240, 288);
		AndroidGraphics g = game.getGraphics();
		g.drawImage(Asset.UI_Exit);
		g.drawImage(Asset.btn_yes);
		g.drawImage(Asset.btn_no);
	}
	// ve chuoi so s tai vi tri bat dau X, Y
	void drawStringNumber (String s, int startX, int startY) {
		
		int len  = s.length();
		int UNIT_CHAR = 24;
		for (int i=0;i<len;i++){
			drawNumber(s.charAt(i),startX+UNIT_CHAR*i,startY);
		}
	}

	// ve time
	void drawTime (int time, int StartX, int StartY) {
		int realTime = time/40;
		String o = "0";
		
		if (realTime/60>9) o = ""; else o = "0";
		minus  = o +  realTime/60;
		
		if (realTime%60>9) o = ""; else o = "0";
		second = o + realTime%60;
		
		drawStringNumber (minus,StartX,StartY);
		drawStringNumber (":",StartX+48,StartY);
		drawStringNumber (second,StartX+44+12,StartY);
	}
	// ve ngoi sao
	void drawStar (int rate, int startX, int startY) {
		AndroidGraphics g = game.getGraphics();
		int len  = 3;
		int UNIT_CHAR = 36;
		for (int i=0;i<len;i++){
			if (i<rate)
			g.drawImage(Asset.icon_star.bitmap,startX+UNIT_CHAR*i,startY);
			else 
				g.drawImage(Asset.icon_star_dis.bitmap,startX+UNIT_CHAR*i,startY);
		}
	}
	// ve diem tai vi tri X,Y;
	public void drawScore(String score, int startX, int startY){
		String o ="0000";
		int len  = score.length();
		score = o.substring(len) + score;
		
		int UNIT_CHAR = 24;
		
		for (int i=0;i<4;i++){
			drawNumber(score.charAt(i),startX+i*UNIT_CHAR,startY);
		
		}
	}
	// kiem tra ki han
	public boolean timeExpired (int startTime, int duration)
	{
		if ((int)System.nanoTime() - startTime >= duration) 
		{  return true;}
		return false;
	}


	
	
}