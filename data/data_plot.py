import matplotlib.pyplot as plt
import math
fileName = 'single_inside'
gry_m = []
acc_y = []
acc_x = []
acc_z = []
acc_m = []
angle = []

with open(fileName) as fp:
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
            if name == 'acc_m':
                acc_m.append(line[p+9:])
            if name == 'angle':
                angle.append(line[p+9:])
# plot
fig = plt.figure('gry and angle')
ax1 = plt.subplot(1,2,1)
ax1.set_title('gry_m')
ax1.plot(gry_m,'yo',gry_m,'k')
ax2 = plt.subplot(122)
ax2.set_title('angle')
ax2.plot(angle,'go',angle,'k')
fig = plt.figure('acc')
ax1 = plt.subplot(2,2,1)
ax1.set_title('acc_x')
ax1.plot(acc_x,'yo',acc_x,'k')
ax2 = plt.subplot(2,2,2)
ax2.set_title('acc_y')
ax2.plot(acc_y,'go',acc_y,'k')
ax2 = plt.subplot(2,2,3)
ax2.set_title('acc_z')
ax2.plot(acc_z,'go',acc_z,'k')
ax2 = plt.subplot(2,2,4)
ax2.set_title('acc_m')
ax2.plot(acc_m,'go',acc_m,'k')
plt.show()
