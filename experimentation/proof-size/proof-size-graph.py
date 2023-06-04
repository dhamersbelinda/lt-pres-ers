import math
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib import rc
from matplotlib.ticker import (MultipleLocator, AutoMinorLocator)
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

data2 = pd.read_csv("proof_size_B2_L1-1024-1", sep=' ')
data3 = pd.read_csv("proof_size_B3_L1-1024-1", sep=' ')
data4 = pd.read_csv("proof_size_B4_L1-1024-1", sep=' ')
data5 = pd.read_csv("proof_size_B5_L1-1024-1", sep=' ')
data10 = pd.read_csv("proof_size_B10_L1-1024-1", sep=' ')
data20 = pd.read_csv("proof_size_B20_L1-1024-1", sep=' ')
# exp = np.arange(1, 10, 1,dtype='int64')

# x = np.power(2, exp)

# X = np.arange(2, 21, 1)
# Y = np.arange(2, 402, 2)
# X, Y = np.meshgrid(X, Y)
# Z = reduced_hash_tree_size(X, Y)
# print(reduced_hash_tree_size(X, Y))

# x_lim = 1400
# x = np.arange(1, x_lim * 1.1, 1, dtype='int64')
# y_2 = reduced_hash_tree_size(2,x)
# y_3 = reduced_hash_tree_size(3,x)
# y_5 = reduced_hash_tree_size(5,x)
# y_10 = reduced_hash_tree_size(10,x)
# y_20 = reduced_hash_tree_size(20,x)
# y_40 = reduced_hash_tree_size(40,x)

fig = plt.figure(figsize=(10,5.5))
ax = fig.add_subplot(1,1,1)

plt.plot(data2['L'], data2['proof_size'], label=r"$B=2$")
plt.plot(data3['L'], data3['proof_size'], label=r"$B=3$")
plt.plot(data4['L'], data4['proof_size'], label=r"$B=4$")
plt.plot(data5['L'], data5['proof_size'], label=r"$B=5$")
plt.plot(data10['L'], data10['proof_size'], label=r"$B=10$")
plt.plot(data20['L'], data20['proof_size'], label=r"$B=20$")

ax.set_xlabel(r'$L$',fontsize=19)
ax.set_ylabel(r'Proof size [Bytes]',fontsize=19)

ax.tick_params(labelsize=15)
ax.xaxis.set_major_locator(MultipleLocator(100))
ax.xaxis.set_minor_locator(MultipleLocator(25))

ax.yaxis.set_major_locator(MultipleLocator(1000))
ax.yaxis.set_minor_locator(MultipleLocator(250))
# plt.xticks([0,32,64,128,256,512,1024])
# plt.xlim([-0.03 * x_lim, x_lim])

# plt.loglog()
plt.grid(True, color='gray', alpha=0.35, linestyle='-', linewidth=0.3)
plt.legend(ncol=2, loc='upper left', fontsize=15, framealpha=0.9)

fig.tight_layout()
plt.show()