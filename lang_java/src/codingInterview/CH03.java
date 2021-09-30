package codingInterview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import common.Solution;

public class CH03 extends Solution {

	@Override
	public void solution() throws IOException {
		int[][] data = {{1,2,3}, {4,5,6,3,4}, {1}};
		// q1(data);
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//q2();
		q3();
	}
	static void q1(int[][] data) {
		class MultipleStack {
			int[] stack; // 스택 여러개를 하나의 배열로
			int[] stackIndex;
			public MultipleStack(int[][] data) {
				int numStack = data.length;
				int maxLength = 0;
				for(int i = 0; i < numStack; i++) {
					if(maxLength < data[i].length) maxLength = data[i].length;
				}
				this.stack = new int[maxLength*numStack];
				this.stackIndex = new int[numStack];
				for(int i = 0; i < this.stack.length; i++) { // 스택의 인덱스
					for(int j = 0; j < numStack; j++) { // 몇번째 배열인지
						if(data[j].length > i) {
							this.stack[numStack*i + j] = data[j][i];
							this.stackIndex[j]++;
						}
					}
				}
			}
			public int pop(int numStack) {
				if(this.isEmpty(numStack)) return Integer.MIN_VALUE;
				int data = this.stack[getLastIndex(numStack)];
				this.stackIndex[numStack]--;
				return data;
			}
			public void push(int numStack, int item) {
				this.stackIndex[numStack]++;
				System.out.println("push");
				if(this.stack.length <= getLastIndex(numStack)) {
					this.stack = Arrays.copyOf(this.stack, this.stack.length*2);
				}
				this.stack[getLastIndex(numStack)] = item;
			}
			public int peek(int numStack) {
				if(this.isEmpty(numStack)) return Integer.MIN_VALUE;
				return this.stack[getLastIndex(numStack)];
			}
			public boolean isEmpty(int numStack) {
				return this.stackIndex[numStack] == 0;
			}
			int getLastIndex(int numStack) {
				if(numStack >= this.stackIndex.length) return -1;
				int stackIndex = this.stackIndex[numStack] - 1; // 해당 스택만 세었을 때 인덱스가 몇번째 까지 있는지
				return stackIndex*this.stackIndex.length + numStack;
			}
			public void print() {
				int numStack = this.stackIndex.length;
				
				for(int i = 0; i < numStack; i++) {
					System.out.print("stack "+i+" : ");
					for(int j = i; j < this.stackIndex[i]*numStack; j += numStack) {
						System.out.print(this.stack[j]+" ");
					}
					System.out.println();
				}
			}
		}
		MultipleStack ms = new MultipleStack(data);
		ms.print();
		System.out.println(ms.pop(1));
		System.out.println(ms.pop(2));
		ms.push(2,2);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.push(2,3);
		ms.print();
	}
	static void q2() {
		class MinStack extends Stack {
			Stack<Integer> minStack;
			public MinStack() {
				this.minStack = new Stack<Integer>();
			}
			public Object pop() {
				//this.minStack.pop();
				int item = (int) super.pop();
				if(min() == item) minStack.pop(); // 같은 경우 스택에서 제거. 같은 경우는 여러번 minStack에 저장되어 있으므로 제거하여도 이슈 없다.
				return item;
			}
			public Object push(int item) {
				if(item <= min()) this.minStack.push(item); // 같거나 작은 경우 스택에 추가
				// this.minStack.push(Math.min(min(), item)); // this.minStack.min()이 아님. this.minStack은 그냥 최소값 저장하는 스택일 뿐임
				return super.push(item);
			}
			int min() {
				if(this.minStack.isEmpty()) return Integer.MAX_VALUE;
				return this.minStack.peek();
			}
		}
		MinStack st = new MinStack();
		st.push(1);
		st.push(2);
		st.push(1);
		st.push(-1);
		st.push(-1);
		st.push(-1);
		st.push(-1);
		System.out.println(st.min());
		st.pop();
		System.out.println(st.min());
		System.out.println(st);
	}
	static void q3() throws IOException {
		class ListStack extends Stack {
			int length;
			ListStack next;
			ListStack prev;
			public ListStack(int length) {
				this.length = length;
				this.next = null;
				this.prev = null;
			}
			public ListStack getLast() {
				ListStack nextStack = this;
				while(nextStack.next != null) nextStack = nextStack.next;
				return nextStack;
			}
			public Object push(Object item) {
				this.length++;
				return super.push(item);
			}
			public Object pop() {
				this.length--;
				return super.pop();
			}
		}
		class SetOfStacks {
			int limit;
			int stackCount;
			ListStack stackList;
			public SetOfStacks(int limit) {
				this.limit = limit;
				this.stackCount = 1;
				this.stackList = new ListStack(0);
			}
			public Object pop() {
				ListStack lastStack = this.stackList.getLast();
				if(lastStack.length == 1) {
					if(lastStack.prev != null) lastStack.prev.next = null;
					this.stackCount--;
				} else if(lastStack.length == 0) { // 없는데 삭제하라고 할때 맨 처음
					lastStack = lastStack.prev;
					if(lastStack == null) return null;
					lastStack.next = null;
				}
				return lastStack.pop();
			}
			public Object push(Object item) {
				ListStack lastStack = this.stackList.getLast();
				if(lastStack.length >= limit) {
					lastStack.next = new ListStack(0);
					lastStack.next.prev = lastStack;
					this.stackCount++;
					return lastStack.next.push(item);
				}
				return lastStack.push(item);
			}
			public Object popAt(int index) { // index 번째 제거
				int stackCount = this.stackCount;
				int numPop = (stackCount-1)*this.limit + this.stackList.getLast().length - index + 1;
				if(numPop <= 0) return null;
				Object[] tmp = new Object[numPop];
				
				for(int i = 0; i < numPop; i++) {
					tmp[i] = this.pop();
				}
				for(int i = numPop-2; i >= 0; i--) {
					this.push(tmp[i]);
				}
				return tmp[0];
			}
			public Object popAt(int index, int presentIndex, int numPop, int StackCount) { 
				if(StackCount == -1) {
					 stackCount = this.stackCount;
					 numPop = (stackCount-1)*this.limit + this.stackList.getLast().length - index + 1;
				}
				if(numPop <= 0) return null;
				if(presentIndex == numPop) {
					return this.pop();
				} else {
					Object tmp = this.pop();
					Object result = this.popAt(index, presentIndex+1, numPop, stackCount);
					this.push(tmp);
					return result;
				}
			}
			public void print() {
				ListStack st = this.stackList;
				while(st != null) {
					System.out.print(st);
					st = st.next;
				}
				System.out.println();
			}
		}
		SetOfStacks st = new SetOfStacks(2);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// String tmp = br.readLine();
		/*while(!tmp.equals("-1")) {
			if(tmp.equals("pop")) {
				st.pop();
				st.print();
			} else if (tmp.equals("p")) {
				st.print();
			} else {
				String[] reg = tmp.split(" ");
				if(reg[0].equals("push")) {
					st.push(Integer.parseInt(reg[1]));
				} else {
					st.popAt(Integer.parseInt(reg[1]));
				}
				st.print();
			}
			tmp = br.readLine();
		}*/
		st.pop();
		st.print();
		st.push(1);
		st.print();
		st.pop();
		st.pop();
		st.print();
		st.push(2);
		st.print();
		st.push(3);
		st.print();
		st.pop();
		st.print();
		//st.popAt(3, 1, -1, -1);
		//st.print();
	}
}
