def toBoard(s):
    s = s.replace('.',' \gy \& ')
    s = s.replace('x',' \\rd \& ')
    s = s.replace('o',' \yw \&')
    s = s.replace('\n', ' \\\\\n\t')
    
    s = '''\\begin{subfigure}[b]{0.4\\linewidth}
	\\centering
	\\begin{tikzpicture}
				\\fill[blue!20] (0,0) rectangle (3.75,3.25);
				\\matrix (mat) at (0,0) [matrix of nodes, ampersand replacement=\\&, nodes={circle, minimum size=.4cm},
				anchor= south west,
				column sep={.1cm}, row sep={.1cm}] {'''+s
    s= s + '''				};
	\\end{tikzpicture}
	\\caption{As player 2}
	\\label{fig:monty}
	\\end{subfigure}%'''
    print(s)
    
    
board = '''..oox..
..xoooo
oxooxxo
xoxxoox
xxxoxox
xooxxox
'''

toBoard(board)