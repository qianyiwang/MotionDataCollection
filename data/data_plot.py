import matplotlib.pyplot as plt
import math
fileName = 'double_inside'
gry_m = []
acc_y = []
acc_x = []
acc_z = []

with open(fileName+'.txt') as fp:
    for line in fp:
        if 'V' in line:
            p = line.index('V')
            name = line[p+2:p+7]
            if name == 'gry_m':
                gry_m.append(float(line[p+9:]))
            if name == 'acc_y':
                acc_y.append(line[p+9:])
            if name == 'acc_x':
                acc_x.append(line[p+9:])
            if name == 'acc_z':
                acc_z.append(line[p+9:])
#
fig = plt.figure('result')
ax1 = plt.subplot(221)
ax1.set_title('gry_m')
ax1.plot(gry_m,'yo',gry_m,'k')
ax2 = plt.subplot(222)
ax2.set_title('acc_x')
ax2.plot(acc_x,'go',acc_x,'k')
ax3 = plt.subplot(223)
ax3.set_title('acc_y')
ax3.plot(acc_y,'go',acc_y,'k')
ax4 = plt.subplot(224)
ax4.set_title('acc_z')
ax4.plot(acc_z,'go',acc_z,'k')
plt.show()
