# coding:utf-8
import matplotlib.pyplot as plt

plt.rcParams['font.sans-serif'] = ['SimHei']  # 用来正常显示中文标签
plt.rcParams['axes.unicode_minus'] = False  # 用来正常显示负号
# 有中文出现的情况，需要u'内容'

fig = plt.figure()

x = [0, 60, 70, 80, 90, 100]
y1 = [0, 0, 0, 0, 1, 1]
y2 = [0, 0, 0, 1, 1, 0]
y3 = [0, 0, 1, 1, 0, 0]
y4 = [0, 1, 1, 0, 0, 0]
y5 = [1, 1, 0, 0, 0, 0]

plt.plot(x, y1, color='r', label='y1')
plt.plot(x, y2, color='g', label='y2')
plt.plot(x, y3, color='k', label='y3')
plt.plot(x, y4, color='m', label='y4')
plt.plot(x, y5, color='b', label='y5')

plt.xlabel(u'X 轴（指标分值）')
plt.ylabel(u'Y 轴（隶属度）')
plt.legend(loc="best")

# plt.rcParams['figure.dpi'] = 300      这行好像没用

fig.savefig('lishu.svg', format='svg')

# plt.show()
