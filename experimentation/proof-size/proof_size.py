import math
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import rc
import os

# Use LaTeX rendering (requires installing MiKTeX on windows + ghostscript)
# Makes the rendering slower tho
plt.rc('text', usetex=True)
# Use LaTeX's default 'Computer Modern' font for labels as well
plt.rc('font', family='serif')

# Storage approximations
# All in Bytes
def ers_storage(n_docs, B, overhead_per_node=64+20, ts_size=2628):
    L = np.ceil(np.emath.logn(B, n_docs))+1
    tot_nodes = (B*L - 1)/(B - 1)
    return tot_nodes * overhead_per_node + ts_size

def classic_storage(n_docs, ts_size=2628):
    return n_docs * ts_size

def hash_tree_size(B, L):
    return np.ceil(np.emath.logn(B, L)) * (B-1)

def reduced_hash_tree_size(B, L):
    return np.ceil(np.emath.logn(B, L)) * (B-1)
    # return np.emath.logn(B, L) * (B-1)


# exp = np.arange(1, 10, 1,dtype='int64')

# x = np.power(2, exp)

# X = np.arange(2, 21, 1)
# Y = np.arange(2, 402, 2)
# X, Y = np.meshgrid(X, Y)
# Z = reduced_hash_tree_size(X, Y)
# print(reduced_hash_tree_size(X, Y))

x_lim = 1400
x = np.arange(1, x_lim * 1.1, 1, dtype='int64')
y_2 = reduced_hash_tree_size(2,x)
y_3 = reduced_hash_tree_size(3,x)
y_5 = reduced_hash_tree_size(5,x)
y_10 = reduced_hash_tree_size(10,x)
y_20 = reduced_hash_tree_size(20,x)
y_40 = reduced_hash_tree_size(40,x)

fig = plt.figure(figsize=(10,5.5))
ax = fig.add_subplot(1,1,1)

# ax = plt.axes(projection="3d")
# ax.plot_surface(X,Y,Z,cmap='viridis',edgecolor='none')
# ax.plot_wireframe(X,Y,Z,color='black',linewidth=0.8)
# surf = ax.plot_surface(X, Y, Z, cmap=cm.coolwarm, linewidth=0, antialiased=False)

plt.plot(x, y_2, label=r"$B=2$")
plt.plot(x, y_3, label=r"$B=3$")
plt.plot(x, y_5, label=r"$B=5$")
plt.plot(x, y_10, label=r"$B=10$")
plt.plot(x, y_20, label=r"$B=20$")
plt.plot(x, y_40, label=r"$B=40$")

ax.set_xlabel(r'$L$',fontsize=19)
ax.set_ylabel(r'Max. proof size [\#hash values]',fontsize=19)

ax.tick_params(labelsize=15)
plt.xlim([-0.03 * x_lim, x_lim])

# plt.loglog()
plt.grid(True, color='gray', alpha=0.35, linestyle='-', linewidth=0.3)
plt.legend(ncol=3, loc='upper left', fontsize=15, framealpha=0.9)

fig.tight_layout()
plt.show()