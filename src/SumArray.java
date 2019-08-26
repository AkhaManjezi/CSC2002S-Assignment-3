import java.util.concurrent.RecursiveTask;

public class SumArray extends RecursiveTask<Vector>  {
	  int lo; // arguments
	  int hi;
	  Vector[] arr;
	  static final int SEQUENTIAL_CUTOFF=4000;

	  Vector ans = new Vector(0,0); // result
	    
	  SumArray(Vector[] a, int l, int h) {
	    lo=l; hi=h; arr=a;
	  }


	  protected Vector compute(){// return answer - instead of run
		  if((hi-lo) < SEQUENTIAL_CUTOFF) {
			  Vector ans = new Vector(0,0);
		      for(int i=lo; i < hi; i++) {
				  ans.x += arr[i].x;
				  ans.y += arr[i].y;
			  }
		      return ans;
		  }
		  else {
			  SumArray left = new SumArray(arr,lo,(hi+lo)/2);
			  SumArray right = new SumArray(arr,(hi+lo)/2,hi);
			  
			  // order of next 4 lines
			  // essential – why?
			  left.fork();
			  Vector rightAns = right.compute();
			  Vector leftAns  = left.join();
			  return leftAns.combine(rightAns);
		  }
	 }
}


