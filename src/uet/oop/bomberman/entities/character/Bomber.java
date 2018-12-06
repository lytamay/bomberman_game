package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Kiểm tra xem có đặt được bom hay không? nếu có thì đặt bom tại vị trí hiện tại của Bomber
     */
    private void detectPlaceBomb()throws NullPointerException {
        // TODO: kiểm tra xem phím điều khiển đặt bom có được gõ và giá trị _timeBetweenPutBombs, Game.getBombRate() có thỏa mãn hay không
        // TODO:  Game.getBombRate() sẽ trả về số lượng bom có thể đặt liên tiếp tại thời điểm hiện tại
        // TODO: _timeBetweenPutBombs dùng để ngăn chặn Bomber đặt 2 Bomb cùng tại 1 vị trí trong 1 khoảng thời gian quá ngắn
        // TODO: nếu 3 điều kiện trên thỏa mãn thì thực hiện đặt bom bằng placeBomb()
        // TODO: sau khi đặt, nhớ giảm số lượng Bomb Rate và reset _timeBetweenPutBombs về 0
        if(_input.space  && Game.getBombRate() >=0 && this._timeBetweenPutBombs <0){
            placeBomb(_x,_y); // tao mot bomb
            Game.addBombRate(-1); // sau khi ma tao xong bom thi lai gan  giam bomdate di 1;
            // khoi tao timebetweenputbom = 30 sau moi lan update thi gia tri giam di 1;
            _timeBetweenPutBombs =30;
            update();
        }
    }

    protected void placeBomb(double x, double y) {
        Bomb b = new Bomb(Coordinates.pixelToTile(x+8), Coordinates.pixelToTile(y-8),_board);
        _board.addBomb(b);
        // TODO: thực hiện tạo đối tượng bom, đặt vào vị trí (x, y)
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    @Override
    public void kill() {
        if (!_alive) return;
        _alive = false;
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            _board.endGame();
        }
    }

    @Override
    protected void calculateMove() {
        // lay gia tri nhan voi toc do cua bomber dang di chuyen;
        double speed = Game.getBomberSpeed();
        int x= 0;
        int y=0;
        if(_input.up) y--;
        if(_input.down) y++;
        if(_input.left) x--;
        if(_input.right) x++;
        if(x!=0|| y!=0){
            // thay doi vi tri cua bomber;
            move(x*speed,y*speed);
            _moving = true;
        }
        else _moving = false;

        // TODO: xử lý nhận tín hiệu điều khiển hướng đi từ _input và gọi move() để thực hiện di chuyển
        // TODO: nhớ cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển
    }

    @Override
    public boolean canMove(double x, double y) {
        // TODO: kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không
        int n=6;
        double xr = _x, yr = _y -16;// tru di do dai cua hinh
        if(_direction ==0) {
            yr += _sprite.getSize()-1;// lay do dai cua anh xong dem -1; de lay toa do cua nhan vat
            xr +=_sprite.getSize()-n;

        }
        if (_direction ==1){
            xr +=1;
            yr+=_sprite.getSize()-n;
        }
        if (_direction ==2){
            xr +=_sprite.getSize()-n;
            yr+=1;
        }
        if (_direction==3){
            xr +=_sprite.getSize()-1;
            yr +=_sprite.getSize()-n;
        }
        int xx = Coordinates.pixelToTile(xr) +(int)x;
        int yy =Coordinates.pixelToTile(yr) +(int)y;
        Entity a = _board.getEntity(xx,yy,this);
        return a.collide(this);
 //      return true;
//        for (int c = 0; c < 4; c++) { //colision detection for each corner of the player
//            double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE; //divide with tiles size to pass to tile coordinate
//            double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE; //these values are the best from multiple tests
//
//            Entity a = _board.getEntity(xt, yt, this);
//
//            if(!a.collide(this))
//                return false;
  //      }

 //       return true;
       }
    @Override
    public void move(double xa, double ya) {

        // xa, ya la so don vi khoang cach muon den
        if(xa <0) _direction = 3;
        if(xa>0) _direction =1;
        if(ya>0) _direction = 2;
        if(ya<0) _direction =0;
        // neu sang trai hoac phai ma co the di dc thi ta cong them _x+=xa;
        if(canMove(xa,0) ==true){
            _x += xa;
        }
        // neu len hoac xuong ma co the di chuyen dc  thi ta cong them gia tri ban dau voi ya;
        if(canMove(0, ya) == true){
            _y +=ya;
        }
        // TODO: sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không và thực hiện thay đổi tọa độ _x, _y
        // TODO: nhớ cập nhật giá trị _direction sau khi di chuyển
    }

    @Override
    public boolean collide(Entity e) {
        // can phai sua lai;
        if(e instanceof Flame){ // neu ma bomber ma va cham voi ngot lua thi se bi tieu diet
            kill();
            return false;
        }
        if(e instanceof Enemy){ // neu bomber  va cham voi enemy thi se bi tieu diet;
            kill();     // thi chet;
            return true;
        }
        // TODO: xử lý va chạm với Flame
        // TODO: xử lý va chạm với Enemy
        return true;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}
