import numpy as np


def yxgx_to_mhyz(yxgx):
    n = yxgx.shape[0]
    row_sums = yxgx.sum(axis=1)

    mhyz = np.zeros((n, n), dtype=np.float64)
    for i in range(n):
        for j in range(n):
            mhyz[i, j] = (row_sums[i] - row_sums[j]) / (2 * n) + 0.5

    return mhyz


# foo = np.array(
#     [0.5, 0.8, 0.6, 0.7, 0.9, 0.2, 0.5, 0.2, 0.3, 0.9, 0.4, 0.8, 0.5, 0.7, 0.8, 0.3, 0.7, 0.3, 0.5, 0.6, 0.1, 0.1, 0.2,
#      0.4, 0.5])
# foo = foo.reshape(5, 5)
# foo = foo.T
#
# print(yxgx_to_mhyz(foo))


def s_test(n, row_sum):
    alpha = (n - 1) / 2
    return (1 / n - 1 / (2 * alpha) + row_sum / (n * alpha)) / 10


# print(s_test(int(input()), float(input())))
