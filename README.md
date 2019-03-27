# IMOS
Improved Meta-aligner and Minimap2 On Spark.

IMOS is an aligner for mapping noisy long reads to the reference genome. It can be used on a single node as well as on distributed nodes. In its single-node mode, IMOS is an Improved version of Meta-aligner (IM) enhancing both its accuracy and speed. IM is up to 6x faster than the original Meta-aligner. It is also implemented to run IM and Minimap2 on Apache Spark for deploying on a cluster of nodes. Moreover, multi-node IMOS is faster than SparkBWA while executing both IM (1.5x) and Minimap2 (25x)

## Citation:
> Hadadian Nejad Youesfi, Mostafa, et al. "IMOS: improved Meta-aligner and Minimap2 On Spark". BMC Bioinformatics. (2019): <span style="color: #0000ff;"><a style="color: #0000ff;" href="https://doi.org/10.1186/s12859-018-2592-5" target="_blank">link</a></span>.

Contact : [hadadian@ce.sharif.edu](mailto:hadadian@ce.sharif.edu)

IMOS can be downloaded from <span style="color: #0000ff;"><a style="color: #0000ff;" href="http://ce.sharif.edu/~hadadian/IMOS.html" target="_blank">here</a></span>.

Pre-Generated human genome index files can be downloaded from <span style="color: #0000ff;"><a style="color: #0000ff;" href="http://ce.sharif.edu/~hadadian/hgindex.html" target="_blank">here</a></span>. (in command line enter hg.fa as index after -REF)

### Index Builder
For building index files from an FA file, place SureMap-IndexBuilder and Reference file in FASTA format in the same directory as IMOS.jar. Currently, it is tested on 64 bit Linux.
```
Usage: java -cp IMOS.jar IndexBuilder [FA File]
        FA File :         FastA Reference File
```
### Load Balancing
Before putting file to the HDFS, use the load balancer to reach better performance. The program will build a .fastm file which is balanced base on the HDFS operations. In case you used this, add -FM in the command when submitting job to spark.
```
Usage: java -cp IMOS.jar LoadBalancer [aligner] [filename] [node] [isIllumina]
        aligner:              [mini,meta]
        filename [string]:    path to the input FastQ file.
        node [int]:           indicates number of nodes in the cluster
        isIllumina:           yes, if it is illumina, No or leaving it blank for pacbio
```

### IMOS Single Node Mode - IM
This mode is designed and developed for single node use. When you do not want to use Apache Spark, use this mode.
```
Usage: java -cp IMOS.jar IM [OPTIONS] -I [inputFQ] -REF [index]
        inputFQ:              Input reads in FastQ format
        index:                Index files name built with index builder
    OPTIONS:
        -C [int]:             Number of cores
        -ER [float]:          Tolerable error rate, 0<=rate<=1
        -O [String]:          Output file path
        -RF [int]:            Refine Factor 1<=factor<=10 [default=4]
        -X [String]:          Sequencer Machine : {"Pacbio","Illumina"}
        
    EXAMPLE: java -cp IMOS.jar IMOSClient -c 4 -x Pacbio -O out.sam -I Read.fq -REF chr19.fa
```
## IMOS SPARK Mode (Distributed Mode)
First, you must set up an apache spark cluster. Note that IMOS can operate on any Spark cluster. It only requires running an IMOSWorker on every Spark worker node.
If you want to run Spark locally, we recommend you to use IMOSClient for better performance.
When the cluster setup completed, submit IMOS to the Spark cluster.
Currently, it is tested on Linux.

### IMOSWorker
```
Usage: java -cp IMOS.jar IMOSWorker [ALIGNER] [OPTIONS] -REF [INDEX]
Warning: port 7777 and 7778 must be open
Warning: use -Xmx18G for human genome
    INDEX:
        Index files name built with index builder
    ALIGNER:
        IM : Improved Meta-aligner
        Mini : Minimap2
        Third : 3rd party aligner
    OPTIONS:
        Minimap2:
           The arguments give directly to the Minimap2. See its help for more details.
        Third:
           The arguments give directly to the Third party aligner.
        IM:
           -C [int]:       Number of cores
           -ER [float]:    Tolerable error rate, 0<=rate<=1
           -RF [int]:      Refine Factor, 1<=rate<=10 [default=4]
           -X [String]:    Sequencer Machine : {"Pacbio","Illumina"}
    
    EXAMPLE: java -cp IMOS.jar IMOSWorker im -c 4 -x Pacbio -REF chr19.fa
```
### Minimap2
For compiling Minimap2 in order to work with IMOSWorker, download main.c form <span style="color: #0000ff;"><a style="color: #0000ff;" href="http://ce.sharif.edu/~hadadian/IMOS.html" target="_blank">here</a></span> and the minimap2 package from <span style="color: #0000ff;"><a style="color: #0000ff;" href="https://github.com/lh3/minimap2" target="_blank">Github</a></span>. Copy our modified main.c into the main folder of minimap2 downloaded from GitHub and do the rest as before to compile minimap2. Finally, put minimap2 and IMOSWorker in the same directory.

## Submit IMOS
```
Usage: spark-submit --class IMOS --master [MASTER] --executor-memory 10G --dirver-memory 2G IMOS.jar [ALIGNER] [OPTIONS] -I [inputFQ]
     MASTER: Identify Spark Master local, yarn or ip of spark standalone master
     inputFQ: Input reads in FastQ format
     ALIGNER: IM for Improved Meta-aligner and ThirdParty, Mini for Minimap2</p>
     OPTIONS:
            -FM : if load balancer is used and the file in the hdfs is a fastm format
        Mini:
            No Option is required. The options must be set at the worker nodes.</p>
        IM:
           -ER [float]: Tolerable error rate, 0<=rate<=1
           -O [String]: Output file path
           -X [String]: Sequencer Machine : {"Pacbio","Illumina"}
    
    EXAMPLE: spark-submit --class IMOS --master local --executor-memory 10G --dirver-memory 2G IMOS.jar IM -X Pacbio -I Read.fq -O out.sam
```
<br/>
<br/>

<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>.
