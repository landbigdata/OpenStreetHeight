function [ H_min ] = culcH_main( a,b,focal_length,pixelSize,imgSize,image_points )

% [ H_min ] =culcH_main(14.48,10.43,0.0042,1.4e-6,[3024,4032],image_points)
%%input :
%A B METER
%% focal length in meter
%% pixel size in meter
%% image points 2x6 mat 
%%imaga size [W,H]

numPoints = 6;
camtobuilding = 15;


h = 3:0.5:20;  %%%
numCandidates = length(h);
a = a*ones(1,numCandidates);
b = b*ones(1,numCandidates);


points3D = cell(1,numCandidates);  %%% candidate 3D points (using a,b,h) (meters) for each candidate building
out3D = cell(1,numCandidates); 
out2D  = cell(1,numCandidates); 
for ii = 1:numCandidates
    points3D{ii} = nan(3,numPoints);     
end
Rt = nan(numCandidates,6);  %%% results - row vectors of 6 Rt values [rx,ry,rz,tx,ty,tz] for each candidate
Err = nan(numCandidates,1); %%% results - RMS error 

r0 = EulerAngles2RotationMatrix([0,-pi/4,0]);   %%% initial estimated R and t
t0 = [0;0;camtobuilding];

for ii = 1:numCandidates  
    points3D{ii}(:,1) = [-a(ii); 0;      0    ];
    points3D{ii}(:,2) = [0;      0;      0    ];
    points3D{ii}(:,3) = [0;      0;      b(ii)];
    points3D{ii}(:,4) = [-a(ii); -h(ii); 0    ];
    points3D{ii}(:,5) = [0;      -h(ii); 0    ];
    points3D{ii}(:,6) = [0;      -h(ii); b(ii)];       
    points3D{ii} = r0*points3D{ii} + repmat(t0,1,numPoints);
end

f =focal_length ; % 0.026    %%% focal-length (meters)
% pixelSize = 1.4e-6 ;%1.4e-6; %%% (meters)
%pixelSize=1.4137e-06%elianaaa

%pixelSize = 1.2000e-05;%320,240
f = f/pixelSize;    %%% focal length (pixels) in 3024 x 4032 image 
%f=2986%%exif;
tic;
K = [f,0,0;0,f,0;0,0,1];    %%% camera matrix
points=image_points;
pointsN = NormalizePoints(points,imgSize);
pointsV = pixelSize*[pointsN;f*ones(1,6)]; %%% vectors from camera optical center to 6 marked points




params.maxIterations = 1e4; %%% 1e4 stop condition
params.xTH = 1e-6;          %%% 1e-6 stop condition
params.funcTH = 1e-9;      %%% 1e-10 stop condition
params.stepSizeMethod = 'EXACT_LINE_SEARCH'; %%% 'EXACT_LINE_SEARCH'  'ARMIJO'  'CONSTANT'  'DIMINISHING'
params.CONST_STEP_SIZE = 0.01;%not used
params.EXACT_BETA = 0.5;%% for the step size function(eliana)
params.ARMIJO_BETA = 0.2;%%not used
params.ARMIJO_SIGMA = 1e-4;%%notused
params.EPSILON = 1e-4;% for makeing matrix positive diffnet after chilesky in newtonGauss(eliana)
params.verbose = 0;%% showing more prameters(eliana)
params.visualize = 0;
params.PAUSE_TIME = 0.2;

for ii = 1:numCandidates    
    
    r1 = 0; r2 = 0; r3 = 0;     %%% initial R guess (3 rotation angles)
    t1 = 0; t2 = 0; t3 = 0;    %%% initial t guess (translation)
       
    V0 = [r1,r2,r3,t1,t2,t3];       
    %V0(1:3) = RotationMatrix2EulerAngles(R_coursera);
    %V0(1:3) = [0, 0.487, 0];
    
    CostFunctionEvaluator = @(V)CostFunction_GH(V,pointsN,points3D{ii},f);
    
    %[V1,fVal,output] = GradientDescent(CostFunctionEvaluator,V0,params);
    [V1,fVal,output] = NewtonGauss(CostFunctionEvaluator,V0,params);
       
    
        
    R = EulerAngles2RotationMatrix(V1(1:3));
    t = (V1(4:6))';
    out3D{ii} = R * points3D{ii} + repmat(t,1,size(points,2));
%     if(h(ii)==checkoptimal)
% 
%         H_i=ii;
%     end    
    Proj = K * out3D{ii};    
    Proj = [Proj(1,:)./Proj(3,:); Proj(2,:)./Proj(3,:)];    
    out2D{ii} = Proj;
    Proj = DeNormalizePoints(Proj,imgSize);
    Diff = Proj - points;
    Err(ii) = sum(sqrt(Diff(1,:).^2 + Diff(2,:).^2)) / size(points,2); 
    if(ii==1)
    Err_min=Err(ii);
    elseif(Err_min>=Err(ii))
        H_i=ii;
        H_min=h(ii);
        min_r=R;
        min_t=t;
        min_xydis=(t(1,1)^2+t(3,1)^2)^0.5;
        Err_min=Err(ii);
    end
    Rt(ii,:) = V1;
    

%     figure;imshow(In1);impixelinfo;title(['projection ',num2str(ii)]);
%     hold on; 
%     plot(points(1,:),points(2,:),'*g');
%     plot(Proj(1,:),Proj(2,:),'*r');
%     hold off;
    
    a_vals = out3D{ii}./pointsV;
    err1 = std(out3D{ii}./pointsV);
    err = mean(std(out3D{ii}./pointsV));

end

% vector_err = nan(numCandidates,1);
% for ii = 1:numCandidates
%    % vector_err555(:,ii)=out3D{ii}./pointsV;
%     vector_err(ii,1) = mean(std(out3D{ii}./pointsV));
% end

vector_err = nan(numCandidates,2);
for ii = 1:numCandidates
%vector_err(ii,1) = mean(std(out3D{ii}./pointsV));
 
lambda_vals1 = out3D{ii}./pointsV;
for jj = 1:6
lambda_vals(:,jj) = lambda_vals1(:,jj)/norm(lambda_vals1(:,jj));
end
vector_err(ii,2) = mean(std(lambda_vals));

    if(ii==H_i)
    H_lambda_vals=lambda_vals1;
    H_lambda_norm=lambda_vals;
    H_vector_err=std(lambda_vals);
    H_err=vector_err(ii,2);
    end    
end

end