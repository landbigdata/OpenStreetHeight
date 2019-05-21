function [P1] = DeNormalizePoints(P0,imgSize,pixelSize)

if nargin < 3
    pixelSize = 1;
end

xHalf = imgSize(1)/2;
yHalf = imgSize(2)/2;
Half = min(imgSize)/2;
P1 = P0/(pixelSize*Half);
P1(1,:) = Half * P1(1,:) + xHalf;
P1(2,:) = Half * P1(2,:) + yHalf;
