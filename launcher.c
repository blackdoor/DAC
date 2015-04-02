#include <stdio.h>
#include <string.h>
#include <stdlib.h>

void eatLast(char array[])
{
   int i = 0;
   while (1)
   {
      if(array[i] == NULL)
      {
         array[i-6] = NULL;
         break;
      }
      i++;
   }
}

int main (int argc, char **argv)
{
   FILE *fp;
   char path[50];
   fp = popen("which dh256", "r");
   fgets(path, sizeof(path)-1, fp);
   pclose(fp);
   eatLast(path);
   chdir (path);
   char command[1024];
   int i;
   strcpy( command, "java -jar bin/dh256.jar" );
   for (i = 1; i < argc; i ++)
   {
   	  strcat(command, " ");
   	  strcat(command, argv[i]);
   }
   system(command);

   return(0);
} 