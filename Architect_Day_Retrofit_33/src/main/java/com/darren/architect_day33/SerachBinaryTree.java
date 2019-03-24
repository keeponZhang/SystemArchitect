package com.darren.architect_day33;

/**
 * @创建者 keepon
 * @创建时间 2018/5/27 0027 上午 8:40
 * @描述 ${TODO}
 * @版本 $$Rev$$
 * @更新者 $$Author$$
 * @更新时间 $$Date$$
 */
public class SerachBinaryTree {
	private TreeNode root;

	public SerachBinaryTree() {

	}

	public static void main(int[] args) {
		SerachBinaryTree binaryTree = new SerachBinaryTree();
		binaryTree.put(50);
		binaryTree.put(30);
		binaryTree.put(20);
		binaryTree.put(44);
		binaryTree.put(88);
		binaryTree.put(44);
		binaryTree.put(87);
		binaryTree.put(16);
		binaryTree.put(7);
		binaryTree.put(67);
		midOrder(binaryTree.root);
	}

	/**
	 * 中序遍历——迭代
	 *
	 * @author Administrator
	 */
	public static void midOrder(TreeNode node) {
		if (node == null) {
			return;
		} else {
			midOrder(node.leftChild);
			System.out.println("midOrder data:" + node.getData());
			midOrder(node.rightChild);
		}
	}

	/**
	 * 查找二叉树，添加节点
	 */
	public TreeNode put(int data) {
		TreeNode node = null;
		TreeNode parent = null;
		if (root == null) {
			node = new TreeNode(0, data);
			root = node;
			return root;
		}
		node = root;
		while (node != null) {
			parent = node;
			if (data > node.data) {
				node = node.rightChild;
			} else if (data < node.data) {
				node = node.leftChild;
			} else {
				return node;
			}
		}

		node = new TreeNode(0, data);
		if (data < parent.data) {
			parent.leftChild = node;
		} else {
			parent.rightChild = node;
		}
		node.parent = parent;
		return node;
	}

	class TreeNode {
		private int      key;
		private TreeNode leftChild;
		private TreeNode rightChild;
		private TreeNode parent;
		private int      data;

		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public TreeNode getLeftChild() {
			return leftChild;
		}

		public void setLeftChild(TreeNode leftChild) {
			this.leftChild = leftChild;
		}

		public TreeNode getRightChild() {
			return rightChild;
		}

		public void setRightChild(TreeNode rightChild) {
			this.rightChild = rightChild;
		}

		public TreeNode getParent() {
			return parent;
		}

		public void setParent(TreeNode parent) {
			this.parent = parent;
		}

		public int getData() {
			return data;
		}

		public void setData(int data) {
			this.data = data;
		}

		public TreeNode(int key, int data) {
			this.key = key;
			this.data = data;
			this.leftChild = null;
			this.rightChild = null;
			this.parent = null;
		}
	}

	public TreeNode searchNode(int key) {
		TreeNode node = root;
		if (node == null) {
			return null;
		} else {
			while (node != null && key != node.data) {
				if (key < node.data) {
					node = node.leftChild;
				} else {
					node = node.rightChild;
				}
			}
		}
		return node;
	}

	public void delete(TreeNode node) throws Exception {
		if (node == null) {
			throw new Exception("该节点无法找到");
		} else {
			TreeNode parent = node.parent;
			//被删除的节点无左右孩纸
			if (node.leftChild == null && node.rightChild == null) {
				if (parent.leftChild == node) {
					parent.leftChild = null;
				} else {
					parent.rightChild = null;
				}
			}

			//被删除的节点有左无右
			if (node.leftChild != null && node.rightChild == null) {
				if (parent.leftChild == node) {
					parent.leftChild = node.leftChild;
				} else {
					parent.rightChild = node.leftChild;
				}
			}

			//被删除的节点有右无左
			if (node.leftChild == null && node.rightChild != null) {
				if (parent.leftChild == node) {
					parent.leftChild = node.rightChild;
				} else {
					parent.rightChild = node.rightChild;
				}
			}

			if (node.leftChild != null && node.rightChild != null) {
				//找到后继节点
				TreeNode next = getNextNode(node);
				//删除该后继节点
				delete(next);
				//查找的节点的值设置为后继节点的值
				node.data = next.data;
			}
		}
	}

	private TreeNode getNextNode(TreeNode node) {
		if (node == null) {
			return null;
		} else {
			if (node.rightChild != null) {
				//找某节点的最小关键字
				return getMinTreeeNode(node);
			} else {
				TreeNode parent = node.parent;
				//跳出循环，说明node已经不是其父节点的右节点，已经找到
				while (parent != null && node == parent.rightChild) {
					node = parent;
					parent = parent.parent;
				}
				return parent;
			}
		}
		//		return  null;
	}

	//首先由右节点
	private TreeNode getMinTreeeNode(TreeNode node) {
		if (node == null) {
			return null;
		} else {
			//一直靠左找，可以找到最小的
			while (node.leftChild != null) {
				node = node.leftChild;
			}
		}
		return node;
	}





}
