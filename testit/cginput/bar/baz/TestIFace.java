package bar.baz;

import java.io.Serializable;

import bar.SampleAnnotation;

@SampleAnnotation
public interface TestIFace extends Comparable<Integer>, Cloneable, Serializable {

	public Integer[][][] getAry(Double[][] thing);

}
