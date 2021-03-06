package biz.tomar.storage.gif;

import java.awt.*;
import java.util.Collections;
import java.util.Vector;
import java.util.stream.IntStream;


/**
 * A Sequence of SingleFrames
 */
public class FrameSequence {

	/**
	 * The frames, this sequence consists of.
	 */
	protected SingleFrame[] singleFrames;

	/**
	 * The frame, that is currently subject to editing;
	 */
	protected SingleFrame singleFrame;

	/**
	 * Eventlisteners
	 */
	private Vector<FrameSequenceListener> frameSequenceListeners = new Vector<>();

	/**
	 * Create a new FrameSequence
	 *
	 * @param singleFrames the frames of the sequence. This must contain at least one
	 *                     element.
	 */
	public FrameSequence(SingleFrame[] singleFrames) {
		this.setSingleFrames(singleFrames);
	}

	/**
	 * Add a frame to the sequence
	 *
	 * @param frame frame to add
	 * @param index where to add
	 */
	public void add(SingleFrame frame,
	                int index) {
		SingleFrame[] singleFrames = getSingleFrames();
		if (index >= 0 && index <= singleFrames.length) {
			SingleFrame[] bigger = new SingleFrame[singleFrames.length + 1];
			// Copy the first few old ones over
			System.arraycopy(singleFrames,
			                 0,
			                 bigger,
			                 0,
			                 index);
			bigger[index] = frame; // Add the new frame
			// Copy the rest of the old ones over
			if (bigger.length - index + 1 >= 0) {
				System.arraycopy(singleFrames,
				                 index + 1 - 1,
				                 bigger,
				                 index + 1,
				                 bigger.length - index + 1);
			}
			setSingleFrames(bigger);
			fireDataChanged();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the index of the currently selected frame
	 */
	public int getSelectedIndex() {
		SingleFrame[] singleFrames = getSingleFrames();
		for (int i = 0;
		     i < singleFrames.length;
		     ++i) {
			if (singleFrames[i] == singleFrame) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Remove a frame from the sequence
	 *
	 * @param frame the frame to remove
	 */
	public void remove(SingleFrame frame) {
		SingleFrame[] singleFrames = getSingleFrames();
		if (singleFrames.length == 1 && frame == singleFrames[0]) {
			setSingleFrames(new SingleFrame[0]);
			fireDataChanged();
			return;
		}
		if (singleFrames.length == 0) {
			return;
		}
		Vector<SingleFrame> tmp = new Vector<>();
		Collections.addAll(tmp,
		                   singleFrames);
		tmp.remove(frame);
		setSingleFrames(new SingleFrame[tmp.size()]);
		tmp.copyInto(singleFrames);
		setSingleFrame(singleFrames[0]);
		fireDataChanged();
	}

	/**
	 * Query the required dimension of a canvas able to display all frames
	 * without cutting anything of (assumed, they are all offesetted at 0,0).
	 *
	 * @return the biggest x and y dimension found among all frames of the
	 * sequence.
	 */
	public Dimension getExpansion() {
		Dimension ret = new Dimension(1,
		                              1);
		SingleFrame[] singleFrames = getSingleFrames();
		for (SingleFrame frame : singleFrames) {
			Dimension d = frame.getSize();
			if (d.width > ret.width) {
				ret.width = d.width;
			}
			if (d.height > ret.height) {
				ret.height = d.height;
			}
		}
		return ret;
	}

	/**
	 * Move a frame in the sequence to a sooner or later position
	 *
	 * @param frame  the frame to move
	 * @param sooner if true, move frame to a sooner position
	 */
	public void move(SingleFrame frame,
	                 boolean sooner) {
		try {
			int           idx          = 0;
			SingleFrame[] singleFrames = getSingleFrames();
			while (singleFrames[idx] != frame) {
				idx++;
			}
			SingleFrame tmp;
			if (sooner) {
				tmp = singleFrames[idx - 1];
				singleFrames[idx - 1] = singleFrames[idx];
			} else {
				tmp = singleFrames[idx + 1];
				singleFrames[idx + 1] = singleFrames[idx];
			}
			singleFrames[idx] = tmp;
			fireDataChanged();
		} catch (Exception e) {
			// Lazy way
		}
	}

	/**
	 * Register with this FrameSequence to be notified of datachanges
	 *
	 * @param fsl Object ot be notifed of datachange events
	 */
	public void addFrameSequenceListener(FrameSequenceListener fsl) {
		Vector<FrameSequenceListener> frameSequenceListeners = getFrameSequenceListeners();
		frameSequenceListeners.add(fsl);
	}

	/**
	 * Deregister listener
	 *
	 * @param frameSequenceListener listener to remove
	 */
	public void removeFrameSequenceListener(FrameSequenceListener frameSequenceListener) {
		Vector<FrameSequenceListener> frameSequenceListeners = getFrameSequenceListeners();
		frameSequenceListeners.remove(frameSequenceListener);
	}

	/**
	 * Notify frameSequenceListeners, that the data changed
	 */
	protected void fireDataChanged() {
		Vector<FrameSequenceListener> frameSequenceListeners = getFrameSequenceListeners();
		int                           size                   = frameSequenceListeners.size();
		IntStream.range(0,
		                size)
		         .forEach(i -> frameSequenceListeners.get(i)
		                                             .dataChanged(this));
	}

	/**
	 * The frames, this sequence consists of.
	 */
	public SingleFrame[] getSingleFrames() {
		return singleFrames;
	}

	public void setSingleFrames(SingleFrame[] singleFrames) {
		this.singleFrames = singleFrames;
		setSingleFrame(singleFrames[0]);
	}

	/**
	 * The frame, that is currently subject to editing;
	 */
	public SingleFrame getSingleFrame() {
		return singleFrame;
	}

	public void setSingleFrame(SingleFrame singleFrame) {
		this.singleFrame = singleFrame;
	}

	/**
	 * Eventlisteners
	 */
	public Vector<FrameSequenceListener> getFrameSequenceListeners() {
		return frameSequenceListeners;
	}

	public void setFrameSequenceListeners(Vector<FrameSequenceListener> frameSequenceListeners) {
		this.frameSequenceListeners = frameSequenceListeners;
	}
}