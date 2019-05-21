function [P1] = NormalizePoints(P0,imgSize,pixelSize)

if nargin < 3
    pixelSize = 1;
end

xHalf = imgSize(1)/2;
yHalf = imgSize(2)/2;
Half = min(imgSize) / 2;
P1(1,:) = (P0(1,:) - xHalf) / Half;
P1(2,:) = (P0(2,:) - yHalf) / Half;
P1 = pixelSize*Half*P1;
