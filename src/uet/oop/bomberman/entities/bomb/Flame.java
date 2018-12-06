package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.graphics.Screen;

public class Flame extends Entity {

	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	/**
	 *
	 * @param x hoành độ bắt đầu của Flame
	 * @param y tung độ bắt đầu của Flame
	 * @param direction là hướng của Flame
	 * @param radius độ dài cực đại của Flame
	 */
	public Flame(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		createFlameSegments();
	}

	/**
	 * Tạo các FlameSegment, mỗi segment ứng một đơn vị độ dài
	 */
	private void createFlameSegments() {
		/**
		 * tính toán độ dài Flame, tương ứng với số lượng segment
		 */
		_flameSegments = new FlameSegment[calculatePermitedDistance()];

		/**
		 * biến last dùng để đánh dấu cho segment cuối cùng
		 */
		boolean last = false;
		int x= (int)_x;
		int y= (int)_y;
		for(int i=0;i<_flameSegments.length;i++){
		    last = i ==_flameSegments.length-1 ? true: false;
	        switch (_direction){
                case 0 : y--; break;
                case 1 : x++; break;
                case 2 : y++; break;
                case 3 : x--; break;
            }
            _flameSegments[i] = new FlameSegment(x,y,_direction,last);
        }
		// TODO: tạo các segment dưới đây
	}
	/**
	 * Tính toán độ dài của Flame, nếu gặp vật cản là Brick/Wall, độ dài sẽ bị cắt ngắn
	 * @return
	 */
	private int calculatePermitedDistance() {
	    int radius = 0;
	    int x = (int)_x;
	    int y = (int)_y;
	    while(radius<_radius){
	        switch (_direction){
                case 0: y--; break;
                case 1: x++; break;
                case 2: y++; break;
                case 3: x--; break;
            }
            Entity e =_board.getEntityAt(x,y);
	        if(e instanceof Grass) {
	            radius++;
            }
            if(this.collide(e)==false){
                break;
            }
            if(e instanceof Brick){
                break;
            }
            radius++;
        }
		// TODO: thực hiện tính toán độ dài của Flame
		return radius;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (int i = 0; i < _flameSegments.length; i++) {
			if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
				return _flameSegments[i];
		}
		return null;
	}

	@Override
	public void update() {}
	
	@Override
	public void render(Screen screen) {
		for (int i = 0; i < _flameSegments.length; i++) {
			_flameSegments[i].render(screen);
		}
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof LayeredEntity){
			if(((LayeredEntity)e).getTopEntity() instanceof Brick){
				Entity e1 = ((LayeredEntity)e).getTopEntity();
				((Brick)e1).destroy(); // gach bi pha huy
				_board.addPoints(10);
			}
			e.update();
			return true;
		}
		else if(e instanceof Wall){ // neu la tuong thi tra ve false;
			return false;
		}
		else if (e instanceof Bomber){
			((Bomber)e).kill();
			return true;
		}
		// TODO: xử lý va chạm với Bomber, Enemy. Chú ý đối tượng này có vị trí chính là vị trí của Bomb đã nổ
		else if(e instanceof Enemy){
			((Enemy) e).kill();
			return true;
		}
		return true;
	}
}
