# Learning of sky take-out
You may know the training company **itheima**(it's hard to leave a comment) 
and the project is called a must for Java backend developer to learn(I guess is especially for the **new hand**)

## Features 
I made some **improvement** based on the raw project,which mainly exists in **Mappers**  (For example, I optimized some sql query from O(n) to O(1)).

And you may browse the **commits history** to better know the different modules of this project and the detailed process when building it.

### mapper
If you felt confused when watching the operation in the video that the teacher traversed some lists to do sql query(`IO!`) every iteration,you'll find the improvements in my code.
I used batched operation with `in` keyword in mysql to avoid the behavior mentioned above in the video,especially when the lecturer iterated the id list to send many sql query.Instead,I choosed to send the list as parameter so as to just query once.
And at the end of the course(the module of report),the lecturer still iterated the list to do sql query by date.I used the <foreach><foreach/> to union all query with one io cast.
