package uet.oop.bomberman.entities.character.enemy.ai;

public class AILow extends AI {

	@Override
	public int calculateDirection() {
		// TODO: cài đặt thuật toán tìm đường đi
		return random.nextInt(4); // tra ve so ngau nhien trong khoảng từ 0->3;
	}

}
