package com.tsystems.javaschool.tasks.pyramid;


import java.util.Arrays;
import java.util.List;

public class PyramidBuilder {
    private int getSize(int length){
        if((Math.sqrt(8*length+1)-1)%2==0){
            return  (int)(Math.sqrt(8*length+1)-1)/2;
        }
        return -1;
    }
    /**
     * Builds a pyramid with sorted values (with minimum value at the top line and maximum at the bottom,
     * from left to right). All vacant positions in the array are zeros.
     *
     * @param inputNumbers to be used in the pyramid
     * @return 2d array with pyramid inside
     * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
     */
    public int[][] buildPyramid(List<Integer> inputNumbers) {
        int size = getSize(inputNumbers.size());
        if(size<=0){
            throw new CannotBuildPyramidException();
        }
        int width = 2*size - 1;
        int[][] pyramid = new int[size][width];
        int listLen = inputNumbers.size();
        Integer[] input = inputNumbers.toArray(new Integer[listLen]);
        try {
            Arrays.sort(input);
            pyramid[0][size-1] = input[0];
            int k=1;
            for(int i=1; i<size;i++){
                for( int j= size-1-i;j<=size-1+i;j+=2){
                    pyramid[i][j]= input[k];
                    k++;
                }
            }
        }catch (NullPointerException e){
            throw new CannotBuildPyramidException();
        }

        return pyramid;
    }


}
