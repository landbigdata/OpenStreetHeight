function [R] = EulerAngles2RotationMatrix(n)

R1 = [1, 0, 0; 0 cos(n(1)), sin(n(1)); 0, -sin(n(1)), cos(n(1))];
R2 = [cos(n(2)), 0, -sin(n(2)); 0, 1, 0; sin(n(2)), 0 cos(n(2))];
R3 = [cos(n(3)), sin(n(3)), 0; -sin(n(3)), cos(n(3)), 0; 0, 0, 1];
R = R3 * R2 * R1;