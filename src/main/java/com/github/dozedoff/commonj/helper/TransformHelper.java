/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.helper;

/**
 * Helper class for performing DCT transformations on images.
 * 
 * @author Nicholas Wright
 *
 */
public class TransformHelper {
	private int matrixSize;
	private double[] dctCoefficients;

	/**
	 * Create a new {@link TransformHelper} for a given matrix size.
	 * 
	 * @param matrixSize
	 *            size of the square matrix
	 */
	public TransformHelper(int matrixSize) {
		this.matrixSize = matrixSize;
		initCoefficients();
	}

	private void initCoefficients() {
		dctCoefficients = new double[matrixSize];

		for (int i = 1; i < matrixSize; i++) {
			dctCoefficients[i] = 1;
		}

		dctCoefficients[0] = 1 / Math.sqrt(2.0);
	}

	/**
	 * Perform the DCT calculation on the provided matrix. Based on
	 * http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
	 * 
	 * @param matrix
	 *            to use for the calculation
	 * @return a matrix containing the calculated DCT
	 */
	public double[][] transformDCT(double[][] matrix) {
		int N = matrixSize;

		double[][] F = new double[N][N];
		for (int u = 0; u < N; u++) {
			for (int v = 0; v < N; v++) {
				double sum = 0.0;
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < N; j++) {
						sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI)
								* (matrix[i][j]);
					}
				}
				sum *= ((dctCoefficients[u] * dctCoefficients[v]) / 4.0);
				F[u][v] = sum;
			}
		}

		return F;
	}

	/**
	 * Calculate the average from the submatrix in the top left corner. Term 0x0 (DC value) is not included in the average.
	 * 
	 * @param dctMap
	 *            matrix containing DCT values
	 * @param submatrixSize
	 *            the size of the submatrix to use
	 * @return the average of the matrix, excluding term 0x0
	 * 
	 */
	public static double dctAverage(double[][] dctMap, int submatrixSize) {
		double sum = 0;

		for (int x = 0; x < submatrixSize; x++) {
			for (int y = 0; y < submatrixSize; y++) {
				sum += dctMap[x][y];
			}
		}

		sum -= dctMap[0][0];
		double average = sum / (double) ((submatrixSize * submatrixSize) - 1);

		return average;
	}
}
