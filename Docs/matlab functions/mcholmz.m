function [L,d,e,pneg]=mcholmz(G)
%  Modified Cholesky Factorization:  [L,d,e,pneg]=mcholmz(G)
%
%  Given a symmetric matrix G, find a vector e of "small" norm and
%  lower triangular matrix L, and vector d such that  G+diag(e) is Positive Definite, and 
%
%      G+diag(e) = L*diag(d)*L'
%
%  Also, calculate a direction pneg, such that if G is not PSD, then
%
%      pneg'*G*pneg < 0
%
%  Reference: Gill, Murray, and Wright, "Practical Optimization", p111.
%  Author: Brian Borchers (borchers@nmt.edu)
%  Modification (acceleration):  Michael Zibulevsky   (mzib@cs.technion.ac.il)
%http://faculty.washington.edu/mforbes/doc/pymmf/_build/_generated/api/mmf.math.linalg.cholesky.gmw81.html

n=size(G,1);  %  n gives the size of the matrix.

%
%  gamma, zi, nu, and beta2 are quantities used by the algorithm.  
%
gamma=max(diag(G));
zi=max(max(G-diag(diag(G))));
nu=max([1,sqrt(n^2-1)]);
beta2=max([gamma, zi/nu, 1.0E-15]);


C=diag(diag(G));

L=zeros(n);
d=zeros(n,1);  %  use vestors as diagonal matrices (by Mzib)
e=zeros(n,1);  %%****************** **************


%
%  Loop through, calculating column j of L for j=1:n
%


for j=1:n,

    bb=[1:j-1];
    ee=[j+1:n];


    if (j > 1),
        L(j,bb)=C(j,bb)./d(bb)';     %  Calculate the jth row of L.  
    end;


    
    if (j >= 2)
        if (j < n), 
            C(ee,j)=G(ee,j)- C(ee,bb)*L(j,bb)';    %  Update the jth column of C.
        end;
    else
        C(ee,j)=G(ee,j);
    end;
    
    
    %%%%     Update theta. 
    
    if (j == n)
        theta(j)=0;
    else
        theta(j)=max(abs(C(ee,j)));
    end;
    
    %%%%%  Update d 
    
    d(j)=max([eps,abs(C(j,j)),theta(j)^2/beta2]');
    
    
    %%%%%%  Update e. 
    
    e(j)=d(j)-C(j,j);                      % Changed by Mzib

    
    
    %%%%%%%  Update C again...

    
    %for i=j+1:n,
    %    C(i,i)=C(i,i)-C(i,j)^2/d(j,j);     % Changed by Mzib
    %end;
    
    ind=[j*(n+1)+1 : n+1 : n*n]';
    C(ind)=C(ind)-(1/d(j))*C(ee,j).^2;


end;

%
% Put 1's on the diagonal of L
%
%for j=1:n,
%    L(j,j)=1;                               % Changed by Mzib
%end;

ind=[1 : n+1 : n*n]';
L(ind)=1;


%
%  if needed, finded a descent direction.  
%
if (nargout == 4)
    [m,col]=min(diag(C));
    rhs=zeros(n,1);
    rhs(col)=1;
    pneg=L'\rhs;
end;


return


%%%%%%%%%%%%%%%%%%%%%% Test %%%%%%%%%%%%%

n=3; G=rand(n);G=G+G';eigG=eig(G), 
[L,d,e,pneg]=mcholmz(G)
E1 = L*diag(d)*L' - G  % Resulting  difference matrix


