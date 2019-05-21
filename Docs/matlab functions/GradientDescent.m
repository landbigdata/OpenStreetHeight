function [x1,f,output] = GradientDescent(func,x0,params)

xCurr = x0;
exitCondition = 0;
numIterations = 0;

[f,g] = func(xCurr);
if params.verbose == 1
    disp(x0');
    disp(f);
    disp('');
end

while exitCondition == 0;
    numIterations = numIterations + 1;    
    prevF = f;    
    [alpha,numIt] = getStepSize(func,xCurr,f,g,-g,params);
    x1 = xCurr - alpha*g;    
    [f,g] = func(x1);    
    
    xDiff = sqrt(sum((x1-xCurr).^2));
    funcDiff = abs(f-prevF);
        
    if numIterations >= params.maxIterations || xDiff < params.xTH || funcDiff < params.funcTH
        exitCondition = 1;
    end  
    if params.verbose == 1
        disp(['itr: ',int2str(numIterations),'  xDiff: ',num2str(xDiff),'  fDiff: ',num2str(funcDiff)]);
        disp(x1');
        disp(f);
        disp('');
    end
    if params.visualize == 1 && length(x0) == 2
        %hold on;plot3(xCurr(1),xCurr(2),f,'*r'); 
        hold on;plot3([xCurr(1),x1(1)],[xCurr(2),x1(2)],[prevF,f],'-*r'); 
        pause(params.PAUSE_TIME);
    end
    
    xCurr = x1;    
    
    if f>prevF
        disp('exit: func increasing');
        break
    end
end

output.numIterations = numIterations;
output.xDiff = xDiff;
output.funcDiff = funcDiff;
