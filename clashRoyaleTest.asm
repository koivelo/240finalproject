# Clash Royale Assembly Test Program
# This tests the custom Clash Royale assembly instructions

.data
.text
.globl main

main:
    elpu $t0, 10
    
    # Load troops
    elpu $t1, 4      # hog rider
    elpu $t2, 3      # knight
    
    # Deploy Hog Rider
    dep $t0, $t1
    
    # emote on opponent
    heheheha
    
    # use rage spell  
    rage $t0
    
    # deploy knight
    dep $t0, $t2
    
    # push knight
    push $t2
    
    # arithmetic test
    elpu $t3, 5
    elpu $t4, 3
    gen $t5, $t3, $t4    # $t5 = 5 + 3 = 8
    
    # multiplication (HI/LO reg.) test
    pump $t3, $t4        # 5 * 3 = 15 (in HI/LO)
    col $t6              
    st $t7               
    
    # mirror spell (copy
    mir $t8, $t5         # Copy $t5 to $t8
    
