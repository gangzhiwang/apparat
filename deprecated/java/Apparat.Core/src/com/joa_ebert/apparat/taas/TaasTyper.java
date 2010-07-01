/*
 * This file is part of Apparat.
 * 
 * Apparat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Apparat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Apparat. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2009 Joa Ebert
 * http://www.joa-ebert.com/
 * 
 */

package com.joa_ebert.apparat.taas;

import com.joa_ebert.apparat.abc.Abc;
import com.joa_ebert.apparat.abc.AbcEnvironment;
import com.joa_ebert.apparat.abc.AbcException;
import com.joa_ebert.apparat.abc.AbstractMultiname;
import com.joa_ebert.apparat.abc.ConstantPool;
import com.joa_ebert.apparat.abc.MultinameKind;
import com.joa_ebert.apparat.abc.Namespace;
import com.joa_ebert.apparat.abc.NamespaceKind;
import com.joa_ebert.apparat.abc.multinames.QName;
import com.joa_ebert.apparat.abc.multinames.Typename;
import com.joa_ebert.apparat.taas.constants.TaasGlobalScope;
import com.joa_ebert.apparat.taas.constants.TaasMultiname;
import com.joa_ebert.apparat.taas.types.AnyType;
import com.joa_ebert.apparat.taas.types.ArrayType;
import com.joa_ebert.apparat.taas.types.BooleanType;
import com.joa_ebert.apparat.taas.types.IntType;
import com.joa_ebert.apparat.taas.types.MultinameType;
import com.joa_ebert.apparat.taas.types.NamespaceType;
import com.joa_ebert.apparat.taas.types.NullType;
import com.joa_ebert.apparat.taas.types.NumberType;
import com.joa_ebert.apparat.taas.types.ObjectType;
import com.joa_ebert.apparat.taas.types.StringType;
import com.joa_ebert.apparat.taas.types.TaasType;
import com.joa_ebert.apparat.taas.types.UIntType;
import com.joa_ebert.apparat.taas.types.UnknownType;
import com.joa_ebert.apparat.taas.types.VoidType;

/**
 * 
 * @author Joa Ebert
 * 
 */
public final class TaasTyper
{
	private static final String EMPTY_STRING = "";

	public static AbstractMultiname toAbcType( final TaasType type )
	{
		if( null == type )
		{
			throw new TaasException( "NULL_TYPE" );
		}

		if( type instanceof MultinameType )
		{
			return ( (MultinameType)type ).multiname;
		}

		final QName result = new QName();

		result.namespace = new Namespace( NamespaceKind.PackageNamespace,
				EMPTY_STRING );

		if( ArrayType.INSTANCE == type )
		{
			result.name = "Array";
		}
		if( IntType.INSTANCE == type )
		{
			result.name = "int";
		}
		else if( UIntType.INSTANCE == type )
		{
			result.name = "uint";
		}
		else if( NumberType.INSTANCE == type )
		{
			result.name = "Number";
		}
		else if( BooleanType.INSTANCE == type )
		{
			result.name = "Boolean";
		}
		else if( StringType.INSTANCE == type )
		{
			result.name = "String";
		}
		else if( ObjectType.INSTANCE == type )
		{
			result.name = "Object";
		}
		else if( NamespaceType.INSTANCE == type )
		{
			result.name = "Namespace";
		}
		else if( VoidType.INSTANCE == type )
		{
			result.name = "void";
		}
		else if( AnyType.INSTANCE == type )
		{
			result.name = ConstantPool.EMPTY_STRING;
		}

		return result;
	}

	/**
	 * Converts an abstract multiname to the native Taas type.
	 * 
	 * @param multiname
	 *            The multiname to convert to a Taas type.
	 * 
	 * @return The native Taas type for the given multiname.
	 */
	public static TaasType toTaasType( final AbstractMultiname multiname )
	{
		if( null == multiname )
		{
			return UnknownType.INSTANCE;
		}

		if( multiname.kind == MultinameKind.QName )
		{
			final QName qname = (QName)multiname;

			if( qname.namespace.kind == NamespaceKind.PackageNamespace )
			{
				if( qname.namespace.name.equals( EMPTY_STRING ) )
				{
					final String name = qname.name;

					if( name.equals( "Array" ) )
					{
						return ArrayType.INSTANCE;
					}
					if( name.equals( "int" ) )
					{
						return IntType.INSTANCE;
					}
					else if( name.equals( "uint" ) )
					{
						return UIntType.INSTANCE;
					}
					else if( name.equals( "Number" ) )
					{
						return NumberType.INSTANCE;
					}
					else if( name.equals( "Boolean" ) )
					{
						return BooleanType.INSTANCE;
					}
					else if( name.equals( "String" ) )
					{
						return StringType.INSTANCE;
					}
					else if( name.equals( "Object" ) )
					{
						return ObjectType.INSTANCE;
					}
					else if( name.equals( "Namespace" ) )
					{
						return NamespaceType.INSTANCE;
					}
					else if( name.equals( "void" ) )
					{
						return VoidType.INSTANCE;
					}
					else if( name.equals( ConstantPool.EMPTY_STRING ) )
					{
						return AnyType.INSTANCE;
					}
				}
			}
		}

		return new MultinameType( multiname );
	}

	private final AbcEnvironment environment;
	private final Abc abc;

	public TaasTyper( final Abc abc, final AbcEnvironment environment )
	{
		this.abc = abc;
		this.environment = environment;
	}

	public TaasType baseOf( final MultinameType type )
	{
		if( type.multiname.kind != MultinameKind.QName )
		{
			throw new TaasException( "Multiname has to be of kind QName." );
		}

		final QName name = (QName)type.multiname;

		try
		{
			return toTaasType( environment.baseType( name ) );
		}
		catch( final AbcException exception )
		{
			throw new TaasException( exception );
		}
	}

	public TaasType baseOf( final TaasType type )
	{
		if( type instanceof MultinameType )
		{
			return baseOf( (MultinameType)type );
		}
		else if( type == NullType.INSTANCE )
		{
			return null;
		}
		else if( type == ObjectType.INSTANCE )
		{
			return type;
		}
		else if( type == ArrayType.INSTANCE || type == StringType.INSTANCE
				|| type == NumberType.INSTANCE || type == IntType.INSTANCE
				|| type == UIntType.INSTANCE )
		{
			return ObjectType.INSTANCE;
		}
		else if( type == UnknownType.INSTANCE )
		{
			return AnyType.INSTANCE;
		}

		throw new TaasException( "Can not find base type of " + type + "." );
	}

	public AbcEnvironment.PropertyInfo findProperty(
			final MultinameType object, final MultinameType property )
	{
		try
		{
			return environment.findProperty( object.multiname,
					property.multiname );
		}
		catch( final AbcException exception )
		{
			throw new TaasException( exception );
		}
	}

	public TaasType typeOf( final AbstractMultiname multiname )
	{
		return toTaasType( multiname );
	}

	public TaasType typeOf( final TaasStack scopeStack,
			final TaasMultiname property ) throws TaasException
	{
		if( property.multiname.kind == MultinameKind.QName )
		{
			final QName qname = (QName)property.multiname;

			if( qname.namespace.kind == NamespaceKind.PackageNamespace
					&& qname.namespace.name.length() > 0 )
			{
				return new MultinameType( qname );
			}
		}

		int n = scopeStack.size();
		boolean globalScopeVisited = false;

		TaasType result = UnknownType.INSTANCE;

		while( --n > -1 && result == UnknownType.INSTANCE )
		{
			if( !globalScopeVisited
					&& scopeStack.get( n ) instanceof TaasGlobalScope )
			{
				globalScopeVisited = true;

				return new MultinameType( property.multiname );
			}

			result = typeOf( scopeStack.get( n ), property, false );
		}

		if( !globalScopeVisited && result == UnknownType.INSTANCE )
		{
			// TODO ensure that this type really exists in global scope.
			return new MultinameType( property.multiname );
		}

		return result;
	}

	public TaasType typeOf( final TaasValue object, final int slotIndex )
	{
		QName result = null;

		if( object instanceof TaasGlobalScope )
		{
			result = abc.classes.get( slotIndex ).instance.name;
		}

		if( null == result )
		{
			throw new TaasException( "Could not solve slot " + slotIndex
					+ " on " + object + "." );
		}

		return toTaasType( result );
	}

	public TaasType typeOf( final TaasValue object, final TaasMultiname property )
			throws TaasException
	{
		return typeOf( object, property, true );
	}

	/**
	 * Resolves the type of an object's property.
	 * 
	 * @param object
	 *            The object containing the property.
	 * 
	 * @param property
	 *            The property to resolve on the object.
	 * 
	 * @return The type of the property. UnknownType if unable to determine.
	 */
	public TaasType typeOf( final TaasValue object,
			final TaasMultiname property, final boolean strict )
			throws TaasException
	{
		try
		{
			if( object.getType() instanceof MultinameType )
			{
				final MultinameType objectType = (MultinameType)object
						.getType();

				final AbstractMultiname multiname = objectType.multiname;

				switch( multiname.kind )
				{
					case QName:
					case QNameA:
						return toTaasType( environment.propertyType( multiname,
								property.multiname, strict ) );
					case Typename:// Undocumented, might break in the future!
						return toTaasType( ( (Typename)multiname ).parameters
								.get( 0 ) );
					case MultinameL:
					case RTQName:
					case RTQNameA:
						return UnknownType.INSTANCE;
				}

			}
		}
		catch( final AbcException ex )
		{
			throw new TaasException( ex );
		}

		return UnknownType.INSTANCE;
	}
}