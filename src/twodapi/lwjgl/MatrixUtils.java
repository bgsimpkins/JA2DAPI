/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package twodapi.lwjgl;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Matrix2f;

/**
 *
 * @author ben
 */
public class MatrixUtils
{
    private static Matrix2f rotMatrix = new Matrix2f();
    private static Matrix2f scalMatrix = new Matrix2f();

    public static Matrix2f getRotationMatrix(float theta)
    {
        theta = (float)Math.toRadians(theta);

        rotMatrix.m00 = (float)Math.cos(theta);
        rotMatrix.m01 = (float)Math.sin(theta);
        rotMatrix.m10 = -(float)Math.sin(theta);
        rotMatrix.m11 = (float)Math.cos(theta);
        return rotMatrix;
    }

    public static Matrix2f getScaleMatrix(float x, float y)
    {
        scalMatrix.m00 = x;
        scalMatrix.m01 = 0;
        scalMatrix.m10 = 0;
        scalMatrix.m11 = y;

        return scalMatrix;
    }

    public static void rotateVerts(float theta, Vector2f[] verts)
    {
        for (int i = 0; i < verts.length; i++)
        {
            Vector2f vert = verts[i];
            Matrix2f.transform(getRotationMatrix(theta), vert, vert);
        }
    }

    public static void scaleVerts(float scaleX, float scaleY, Vector2f[] verts)
    {
        for (int i = 0; i < verts.length; i++)
        {
            Vector2f vert = verts[i];
            Matrix2f.transform(getScaleMatrix(scaleX,scaleY), vert, vert);
        }
    }

    public static void translateVerts(float x, float y, Vector2f[] verts)
    {
        for (int i = 0; i < verts.length; i++)
        {
            Vector2f vert = verts[i];
            vert.translate(x, y);
        }
    }

    public static void setVerts(Vector2f[] verts1, Vector2f[] verts2)
    {
        for (int i = 0; i < verts1.length; i++)
        {
            verts2[i].set(verts1[i]);
        }
    }

    public static void main(String[] args)
    {
        Vector2f v1 = new Vector2f(1,2);
        Vector2f v2 = new Vector2f(1,4);
        Vector2f v3 = new Vector2f(3,2);

        Vector2f[] vecs = new Vector2f[]{v1,v2,v3};

        MatrixUtils.rotateVerts((float)(Math.PI/4),vecs);
        //MatrixUtils.scaleVerts(2,2,vecs);

        for (int i = 0; i < vecs.length; i++)
        {
            Vector2f vec = vecs[i];
            System.out.println(i+": x="+vec.x+" y="+vec.y);
        }
    }
}
