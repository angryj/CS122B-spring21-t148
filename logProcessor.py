import sys
#TLDR, python logProcessor.py <relativeFilePath1> <relativeFilePath2> ... <relativeFilePath3>
def process(names):
    totalTS = 0
    totalTJ = 0
    i = 0
    for n in names:
        print("Processing file: {}".format(n))
        try:
            f = open(n, "r")
            line = f.readline()
            
            while line:
                TSTJ = line.split(",")
                TS = float(TSTJ[0])/1000000
                TJ = float(TSTJ[1])/1000000
                totalTS += TS
                totalTJ += TJ
                i += 1
        
                line = f.readline()
            
        except:
            print("Error: invalid file")
            return
    
    TSAverage = totalTS/i
    TJAverage = totalTJ/i

    print("Average TS: {}ms".format(TSAverage))
    print("Average TJ: {}ms".format(TJAverage))
        
if __name__ == "__main__":
    if len(sys.argv) > 1:
        process(sys.argv[1:])