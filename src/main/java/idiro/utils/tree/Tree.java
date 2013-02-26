package idiro.utils.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Implementation of a Tree data stucture.
 * 
 * The tree is composed of:
 * A head: the current element.
 * A parent: a tree that include the current element, the
 * parent is null if the head is the root.
 * SubTrees: the subtree, the children of the current element,
 * the list is empty if the head is a leaf.
 * 
 * @author etienne
 *
 * @param <T>
 */
public class Tree<T extends Comparable<T> > implements Comparable<Tree<T>>{

	/** 
	 * Current element
	 */
	private T head;
	
	/**
	 * Parent element
	 */
	private Tree<T> parent = null;
	
	/**
	 * Children
	 */
	private Set<Tree<T>> subTreeList = new TreeSet<Tree<T>>();


	/**
	 * Constructor to create a root element
	 * @param head
	 */
	public Tree(T head){
		this.head = head;
	}

	/**
	 * Constructor to create a leaf
	 * @param head
	 * @param parent
	 */
	public Tree(T head,Tree<T> parent){
		this.head = head;
		this.parent = parent;
	}

	public T get(){
		return head;
	}

	/**
	 * Find all the elements in the entire tree.
	 * Find the elements equals to element within
	 * the tree (recursive method)
	 * @param element
	 * @return
	 */
	public List<Tree<T>> findInTree(T element){
		Tree<T> root = this;
		while(root.getParent() != null){
			root = root.getParent();
		}
		return root.getChildren(element);
	}

	/**
	 * Find all the elements within the children.
	 * Find the elements equals to element within
	 * the head and its children (recursive method)
	 * @param element
	 * @return
	 */
	public List<Tree<T>> getChildren(T element){
		List<Tree<T>> ans = new LinkedList<Tree<T>>();
		Iterator<Tree<T>> it = subTreeList.iterator();
		while(it.hasNext()){
			Tree<T> cur = it.next();
			if(cur.head.equals(element)){
				ans.add(cur);
			}
			ans.addAll(cur.getChildren(element));
		}
		return ans;
	}


	/**
	 * Add a child.
	 * 
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(Tree<T> e) {
		return subTreeList.add(e);
	}

	/**
	 * Add children.
	 * 
	 * @param arg0
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Tree<T>> arg0) {
		return subTreeList.addAll(arg0);
	}

	/**
	 * @return the parent
	 */
	public Tree<T> getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Tree<T> parent) {
		this.parent = parent;
	}

	/**
	 * @return the head
	 */
	public T getHead() {
		return head;
	}

	/**
	 * @return the subTreeList
	 */
	public Set<Tree<T>> getSubTreeList() {
		return subTreeList;
	}

	@Override
	public int compareTo(Tree<T> arg0) {
		return this.head.compareTo(arg0.head);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean equals(Object o){
		if(o instanceof Tree){
			if(((Tree)o).head.getClass().equals(this.head.getClass())){
				return compareTo((Tree<T>)o) == 0;
			}
		}
		return false;
	}



}
