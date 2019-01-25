package bio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import shortRead.SureMapObjDyn;

public class WGRef implements Serializable{
	public SureMapObjDyn smo0;
	public SureMapObjDyn smo1;
	public boolean doubleRef;
	public WGRef() {
	}

	public void init(Arguments arg, String errorRate, String ref0, String ref1) throws IOException {
		smo0 = new SureMapObjDyn();
		smo0.init(arg, new String[] { "-g", "-u", "-e", errorRate, "-m", arg.getMode(), "-o", "output.sam", ref0, "query.fastq" });
		doubleRef = false;
		if (ref1 != null) {
			smo1 = new SureMapObjDyn();
			smo1.init(arg, new String[] { "-g", "-u", "-e", errorRate, "-m", arg.getMode(), "-o", "output.sam", ref1, "query.fastq" });
			doubleRef = true;
		}
	}
	
	public ArrayList<ALRecord> Align(Iterator<ALRecord> list) throws InterruptedException, FileNotFoundException{
		ArrayList<ALRecord> al = smo0.threadDistribute(list);
		if (doubleRef) {
			ArrayList<ALRecord> notMapped = new ArrayList<ALRecord>();
			for(ALRecord alr : al){
				if(!alr.isMaped()){
					Arrays.fill(alr.getFragPos(), 0);
					notMapped.add(alr);
				}
			}
			smo1.threadDistribute(notMapped.iterator());
		}
		return al;
	}
	public ArrayList<ASRecord> Assign(Iterator<ALRecord> list) throws InterruptedException{
		ArrayList<ASRecord> as = smo0.threadDistributeAssign(list);
		if (doubleRef) {
			ArrayList<ASRecord> notMapped = new ArrayList<ASRecord>();
			for(ASRecord asr : as){
				if(!asr.isMaped()){
					notMapped.add(asr);
				}
			}
			smo1.threadDistributeAssign(notMapped.iterator());
		}
		return as;
	}
	public void destroy() throws IOException{
		smo0.destroy();
		if (doubleRef) {
			smo1.destroy();
		}
	}
}
