/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio;

import java.io.Serializable;

/**
 *
 * @author MOSJAVA
 */
public class Arguments implements Serializable{
    private int FragmentSize;
    private byte Depth;
    private byte CorrDist;
    private int core;
	private int minL;
	private boolean fast;
	private String mode;
	private double errorRate;
    public Arguments() {
        super();
        fast = false;
    }

    /**
     * @return the FragmentSize
     */
    public int getFragmentSize() {
        return FragmentSize;
    }

    /**
     * @param FragmentSize the FragmentSize to set
     */
    public void setFragmentSize(int FragmentSize) {
        this.FragmentSize = FragmentSize;
    }

    /**
     * @return the Depth
     */
    public byte getDepth() {
        return Depth;
    }

    /**
     * @param Depth the Depth to set
     */
    public void setDepth(byte Depth) {
        this.Depth = Depth;
    }

    /**
     * @return the CorrDist
     */
    public byte getCorrDist() {
        return CorrDist;
    }

    /**
     * @param CorrDist the CorrDist to set
     */
    public void setCorrDist(byte CorrDist) {
        this.CorrDist = CorrDist;
    }

	/**
	 * @return the core
	 */
	public int getCore() {
		return core;
	}

	/**
	 * @param core the core to set
	 */
	public void setCore(int core) {
		this.core = core;
	}

	public void setMinL(int i) {
		// TODO Auto-generated method stub
		this.minL = i;
	}

	/**
	 * @return the minL
	 */
	public int getMinL() {
		return minL;
	}

	/**
	 * @return the fast
	 */
	public boolean isFast() {
		return fast;
	}

	/**
	 * @param fast the fast to set
	 */
	public void setFast(boolean fast) {
		this.fast = fast;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the errorRate
	 */
	public double getErrorRate() {
		return errorRate;
	}

	/**
	 * @param errorRate the errorRate to set
	 */
	public void setErrorRate(double errorRate) {
		this.errorRate = errorRate;
	}
    
}
