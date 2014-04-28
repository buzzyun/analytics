package org.fastcatsearch.analytics.analysis.util;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunEntryMergeReader<E extends RunEntry> {
	protected static Logger logger = LoggerFactory.getLogger(RunEntryMergeReader.class);

	private Comparator<E> comparator;

	private int[] heap;
	private List<RunEntryReader<E>> readerList;
	private int runSize;

	private E entry;
	private E entryOld;

	public RunEntryMergeReader(List<RunEntryReader<E>> entryReaderList) {
		this(entryReaderList, null);
	}

	public RunEntryMergeReader(List<RunEntryReader<E>> entryReaderList, Comparator<E> comparator) {
		this.readerList = entryReaderList;
		this.comparator = comparator;

		runSize = entryReaderList.size();
		makeHeap(runSize);
	}

	public E read() {
		E result = null;
		while (true) {
			int idx = heap[1];
			entry = readerList.get(idx).entry();

			 //logger.debug("## check {} / {} : {}", idx, entry, entryOld);

			if (entry == null) {
				if (entryOld == null) {
					// 둘다 null이면 처음부터 entry가 없는것임.
					return null;
				} else {
					// entryOld를 리턴한다.
					result = entryOld;
				}
			} else {
				if (entryOld == null) {
					// ignore
				} else if (entry.equals(entryOld)) {
					entry.merge(entryOld);
					// logger.debug("## merge count {}", entry);
				} else {
					result = entryOld;
				}
			}

			// backup cv to old
			entryOld = entry;

			readerList.get(idx).next();

			heapify(1, runSize);

			if (result != null) {
				return result;
			}

		} // while(true)

	}

	protected void makeHeap(int heapSize) {
		heap = new int[heapSize + 1];
		// index starts from 1
		for (int i = 0; i < heapSize; i++) {
			heap[i + 1] = i;
		}

		int n = heapSize >> 1; // last inner node index

		for (int i = n; i > 0; i--) {
			heapify(i, heapSize);
		}

	}

	protected void heapify(int idx, int heapSize) {

		int temp = -1;
		int child = -1;

		while (idx <= heapSize) {
			int left = idx << 1;// *=2
			int right = left + 1;

			if (left <= heapSize) {
				if (right <= heapSize) {
					int c = compareEntry(left, right);
					// logger.debug("compare result1 >> {}  >> {} : {}", c,
					// left, right);
					if (c < 0) {
						child = left;
					} else if (c > 0) {
						child = right;
					} else {
						child = left;
					}
				} else {
					// if there is no right el.
					child = left;
				}
			} else {
				// no children
				break;
			}

			// compare and swap
			int c = compareEntry(child, idx);
			// logger.debug("compare result2 >>{}  >> {} : {}", c, child, idx);
			if (c < 0) {
				temp = heap[child];
				heap[child] = heap[idx];
				heap[idx] = temp;
				idx = child;
				// System.out.println("idx1="+idx);
			} else if (c == 0) {
				break;
			} else {
				// sorted, then do not check child
				break;
			}

		}
	}

	protected int compareEntry(int one, int another) {
		int a = heap[one];
		int b = heap[another];

		// return compareKey(reader[a].entry(), reader[b].entry());
		int r = compareEntry(readerList.get(a).entry(), readerList.get(b).entry());

		return r;
	}

	private int compareEntry(E entry, E entry2) {
		// logger.debug("compareKey > {} : {}", entry, entry2);
		
		if (comparator != null) {
			return comparator.compare(entry, entry2);
		}
		
		
		if (entry == null && entry2 == null) {
			return 0;
		} else if (entry == null) {
			return 1;
		} else if (entry2 == null) {
			return -1;
		}

		
		// reader gets EOS, returns null
		return entry.compareTo(entry2);
	}
}
