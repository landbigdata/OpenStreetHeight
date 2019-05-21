function [R_photo,R_coursera,n_photo,n_coursera] = find_rotation_angles(f,v1,v2,v3)

%%finding the rotation matrix 
%%return 2 sets of angles that were calculated by 2 different ways

%f-focal length[pixel]- 474 pix for 640x480, 2986 pix for the full image
%v1,v2,v3 vanishing points ,left right up

%%roatation from coursera
kk = [f,0,0;0,f,0;0,0,1];
r1 = kk^-1*v1'/norm((kk^-1*v1'));
r2 = kk^-1*v2'/norm((kk^-1*v2'));
r3 = cross(r1,r2);
r3 = r3/norm(r3);
R_coursera = [r1,r2,r3];

%%R from photo 2 course 
x1 = v1(1); y1 = v1(2);
x2 = v2(1); y2 = v2(2);
x3 = v3(1); y3 = v3(2);
l1 = f /(x1^2 + y1^2 + f^2)^0.5;
l2 = f /(x2^2 + y2^2 + f^2)^0.5;
l3 = f /(x3^2 + y3^2 + f^2)^0.5;
RT = [-l1*x1/f, -l2*x2/f, -l3*x3/f; -l1*y1/f, -l2*y2/f, -l3*y3/f; l1,l2,l3];
R_photo = [RT(:,1),RT(:,2),RT(:,3)];

n_coursera = (180/pi)*RotationMatrix2EulerAngles(R_coursera);
n_photo = (180/pi)*RotationMatrix2EulerAngles(R_photo);

end