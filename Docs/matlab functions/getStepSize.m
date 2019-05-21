function [alpha,numIterations] = getStepSize(func,xCurr,fCurr,gCurr,dCurr,params)

alpha = 1;
exitCondition = 0;
numIterations = 0;

if strcmp(params.stepSizeMethod,'EXACT_LINE_SEARCH') == 1 
    alpha = params.EXACT_BETA;
    while exitCondition == 0;        
        [f] = func(xCurr + alpha*dCurr);
        if f<=fCurr
            exitCondition = 1;
        else            
            alpha = params.EXACT_BETA*alpha;
        end
        numIterations = numIterations + 1;
    end
elseif strcmp(params.stepSizeMethod,'ARMIJO') == 1
    alpha = params.ARMIJO_BETA;
    while exitCondition == 0;
        [f] = func(xCurr + alpha*dCurr);
        if f<=fCurr + alpha*params.ARMIJO_SIGMA*(gCurr'*dCurr);
            exitCondition = 1;
        else
            alpha = params.ARMIJO_BETA*alpha;
        end
        numIterations = numIterations + 1;
    end
elseif strcmp(params.stepSizeMethod,'CONSTANT') == 1
    alpha = params.CONST_STEP_SIZE;
    while exitCondition == 0;
        [f] = func(xCurr + alpha*dCurr);
        if f<=fCurr
            exitCondition = 1;
        else            
            alpha = alpha + params.CONST_STEP_SIZE;
        end
        numIterations = numIterations + 1;
    end
elseif strcmp(params.stepSizeMethod,'DIMINISHING') == 1        
    alpha = 1;
    while exitCondition == 0;
        [f] = func(xCurr + alpha*dCurr);
        if f<=fCurr
            exitCondition = 1;
        else            
            alpha = 1/(numIterations+2);
        end
        numIterations = numIterations + 1;
    end
end
    