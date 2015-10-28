

/**
 * 
 * Se a arvore estiver balanceada a perfomance será O(log n), pois a cada iteracao 
 * a quantidade de busca será reduzida. 
 * 
 * se a arquivo estiver totalmente desbalanceada, a perfomance será O(n). 
 * 
 * A arvore utilizada aqui é uma AVL Tree. 
 * 
 * @author fsoster
 *
 */

class BinaryNode {
	int val; 
	BinaryNode right; 
	BinaryNode left; 
	int height; 
	
	public BinaryNode(int val) {
		this.val = val; 
	}
	
	public void add(int val) {
		
		if (val <= this.val) {
			if(this.left != null) {
				this.left.add(val);
			} else {
				this.left = new BinaryNode(val); 
			}
			
		} else {
			if(this.right != null) {
				this.right.add(val);
			} else {
				this.right = new BinaryNode(val); 
			}
		}
	
		this.height = Math.max(height(left),height(right)) +1; 
	}
	public static int height(BinaryNode n ) {
		return n == null? -1 : n.height; 
	}
}

public class BinarySearchTree {

	BinaryNode root; 
	
	BinarySearchTree() {
		this.root = null; 
	}
	
	public static void add(BinarySearchTree self, int value) {
		if(self.root == null) {
			self.root = new BinaryNode(value); 
		} else {
			self.root.add(value);
		}
		
	}
	
	public static boolean contains(BinarySearchTree self, int value) {
		
		BinaryNode root = self.root; 
		while (root != null) {
			if(value == root.val) {
				return true; 
			} 
			if(value < root.val) {
				root = root.left; 
			} else {
				root = root.right; 
			}
			
		}
		return false; 
	}
	
	public static void main(String...args) {
		BinarySearchTree tree = new BinarySearchTree(); 
	
		add(tree, 1);
	}
	
}
