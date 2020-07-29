import numpy as np


# ROW_SUMS = np.array([0])  # 这个初始值没有意义
# N = 0  # 这个初始值没有意义


# def test(i, j):
#     return i + 7 * j / 3


def get_trans_func(sq_ma):
    n = sq_ma.shape[0]
    row_sums = sq_ma.sum(axis=1)

    def trans_func(i, j):
        return (row_sums[i] - row_sums[j]) / (2 * n) + 0.5

    return trans_func


# def bad_trans_func(i, j):
#     return (ROW_SUMS[i] - ROW_SUMS[j]) / (2 * N) + 0.5


def yxgx_to_mhyz(yxgx):
    shp = yxgx.shape
    n = shp[0]
    # ROW_SUMS = yxgx.sum(axis=1)

    trans_func = get_trans_func(yxgx)
    return np.fromfunction(trans_func, (n, n), dtype=np.float64)

    # return np.fromfunction(bad_trans_func, (N, N), dtype=np.float64)
    # return np.fromfunction(test, (n, n), dtype=np.float64)
    # return trans_func(0,2)


foo = np.array(
    [0.5, 0.8, 0.6, 0.7, 0.9, 0.2, 0.5, 0.2, 0.3, 0.9, 0.4, 0.8, 0.5, 0.7, 0.8, 0.3, 0.7, 0.3, 0.5, 0.6, 0.1, 0.1, 0.2,
     0.4, 0.5])
foo = foo.reshape(5, 5)
foo = foo.T

print(yxgx_to_mhyz(foo))
