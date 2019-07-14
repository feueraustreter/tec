# tec
'**tec**' is a new programming languge that is user friendly as you can just write your Code. You do not have to mess with any **main** functions if you do not want to. If you want to start with tec you do not need to have any knowledge of any programming languages what so ever.
To change to a new language you can use this a ground basis to change to Java, Swift, Python, JavaScript, Go. If you have any knowledge in Java, Swift, Python, Go or JavaScript you should not have any trouble to write programms in **tec** if you know the basics.

## Getting started
Create a file with the suffix **.tec**. Open the file and now you can write your first code. Start with a 'Hello World' programm.
```
print "Hello World"
```
If you execute this Program it will output Hello World.

## Comments
One line Comments start with '//'. There are also Block comment which start with '/\*' at the start of a line and end with '\*/' at the end of a line.
```
// This prints "Hello World"
print "Hello World"

/*Here are some prints for some formating
also this system writes "Hello World" 3 Times*/
print ""
print "Hello World"
print "Hello World"
print "Hello World"
print ""
```

## Datatypes
There are 5 Datatypes. The Datatypes are chr for Character, bol for Boolean, num for Float, int for Integer and str for String.
```
*chr '<One Character here>'
*bol <true/false>
*num <Floating point number>
*int <Non Floating point Number or Hex Number with #>
*str "One or More Characters" or 'Many Character'
```

## Variables
There are 2 types of Variables, constant ones and dynamic ones. Dynamic variables are changeable and can be reassigned.
```
// This Variable is an Integer Variable '0'
var int = 0
var int2 = #1

// This Variable is a Float Variable '0.0'
var num = 0.0

// This Variable is a Character Variable 'c'
var chr = 'c'

// This Variable is a Boolean Variable 'true'
var bol = true

// This Variable is a String Variable 'Hello World'
var str = "Hello World"
var str2 = 'Hello World'

// To Create a Constant instead of using 'var' use 'let'
let str = "HELP"
```

## Advanced Printing
To print a Value of a Variable you can write the Variable name behind the print Statement. The print Statement can also Calculate stuff and you can add Calculations and variables together.
```
// This print will calculate the sum of the value of 'i' and 1
var i = 1
print i + 1

// This print will output "c2"
print "c" + (i+1)

// You can also calculate the sum of 2 Variables
var j = 3
print j + i

// To put String together you can also use +
let k = " World"
print "Hello" + k 
```

## Slowing things down
Use the 'sleep' statement with a milliseconds time behind it to sleep for x milliseconds.
```
print "Starting to Sleep"

sleep 1000

print "Finished to Sleep"
```

## Conditional Statements

#### Basic If Statements
If you want to do choose what you do use 'if'.
```
// Use an Boolean Expression in the Brackets of the if Statement.
if (true) {

}

if (1 == 1) {

}

if (true == false) {

}

if (true || false) {

}
```

#### Intermediary If Statements 
You can also use variables in Boolean Expressions. The easiest is a boolean variable because you do not need to compare it to anything else.
To compare use '==' '!=' '>' '<' '>=' '<='. Also you have some basic and advanced Logic Operators as || (or) && (and) and !! (not). Just have in mind that you need to use '<<' '>>' or '«' '»' as Priority Definition in Boolean Expressions.
Advanced Logic Operators are !& (nand), x| (xor), n| (nor) and xn (xnor).
Have in mind that the Not Operator '!!' needs to be behind the Boolean value and not before it.
You can also have a Boolean Expression after a print Statement.
```
// All Logic Operators
var b1 = true
var b2 = false

print b1 || b2
print b1 && b2
print b1 !!
print b1 !& b2
print b1 x| b2
print b1 n| b2
print b1 xn b2

// Variables in Boolean Expressions
var i1 = 0

if (i1 == 0) {
    print "i1 is zero"
}
```

#### Advanced If Statements
To do something if the if failed you use the 'else' statement. If you want to check if something is something do one thing and if something is something else another thing and if nothing matches do a third things use the 'else if'.
```
var i = 0

if (i == 0) {
    print "i is zero"
} else if (i == 1) {
    print "i is one"
} else {
    print "i is not zero or one"
}
```

## Repeating
To Repeat something multiple times use the 'while' loop. This loop will check first before executing the Loop Part.
```
// Loop through the Numbers from zero to ten

var i = 0
while (i < 10) {
    print "Loop Iteration: " + i
    i = i + 1
}
```

## Functions

#### Definition
Functions are used to declare one Code Snippet that you want to use multiple times throughout your project. The 'func' key will get used. After that there is the name of your function followed by an opening and closing Bracket.
```
// This is a valid function
func example() {

}
```

#### Calling
To call a function use the function name with opening and closing bracket after it.
```
// This is to call the function.
example()

// This is a valid function
func example() {

}
```
You can call the function either above the Definition or below it.
```
// This is a valid function
func example() {

}

// This is to call the function.
example()
```

#### Parameter
A function can have Parameters. These parameters are having a type and a name. To add a Parameter to a function the Parameter type followed by the name needs to be put into the '()' brackets. To have multiple Parameters you need to seperate each one by a colon ','.
```
// Function with one Parameter
func oneParameter(*int i) {

}

// Function with two Parameters
func twoParameters(*int i, *str s) {

}
```
To call a function with Parameters as Input write your input in the call Brackets. The parameter needs to have the same type as the parameter of the function you want to call.
```
// Function with one Parameter
func oneParameter(*int i) {

}

// Function with two Parameter
func twoParameters(*int i, *str s) {

}

// Call the Function 'oneParameter' with the parameter 1
oneParameter(1)

// Call the Function 'twoParameters' with the parameters 1, "Hello"
twoParameters(1, "Hello")
```

#### Function Overloading
You can also define the same Function multiple times with different Parameters. It is not allowed to have the same function twice. So with the same parameter types in the same order.
```
// This defines the function 'overloaded' with a Parameter i of type int
func overloaded(*int i) {

}

// This defines the function 'overloaded' with a Parameter i of type string
func overloaded(*str i) {

}
```
The Executor automatically calls the correct function to your parameters. At least if you have a Function with that kind of parameter configuration.
```
// This defines the function 'overloaded' with a Parameter i of type int
func overloaded(*int i) {

}

// This defines the function 'overloaded' with a Parameter i of type string
func overloaded(*str i) {

}

// This will call the 'overloaded(*int i)' function
overloaded(1)

// This will call the 'overloaded(*str i)' function
overloaded("1")
```

#### Parameter Expressions
You can also use a Parameter with Variables as well as an Expression as parameter.
```
// The Function Declaration
func parameterExpressions(*int i) {

}

// Function call with Expression Syntax
parameterExpressions(1+1)

var i = 0
parameterExpressions(i+1)
```

#### Return Statement
The return statement can be used to exit a function before the real Block Exit of the function. But it can also return some value. To return something with a specific value write an Expression behind it and see what happends.
If you return from the Main Thread it gets discontinued.
```
// This will give the Output '1'
testReturn(1)

// This wont give any Output
testReturn(-1)

return

// This Statement wont be executed.
testReturn(1)

// Dont print value of i if the value is below zero
func testReturn(*int i) {
    if (i < 0) {
        return
    }
    print i
}
``` 

#### Recursion
You can also call the same function within itself. This will create a Recursive Loop.
```
// Call the Method recursion with parameter 3
recursion(3)

// The Declaration of the recursion function with an *int as parameter
func recursion(*int i) {
    // Print "Current value: " + i
    print "Current value: " + i
    if (i > 0) {
        // Call itself if i is greater than zero
        recursion(i - 1)
    }
    // Print "end" + i
    print "end" + i
}
```
The output of the Code above is as follows.
```
Current value: 3
Current value: 2
Current value: 1
Current value: 0
end0
end1
end2
end3
```